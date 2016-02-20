package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

/**
 * The class to parse the yaml format configuration 
 * file using snakeyaml 
 * @author LumiG
 *
 */
public class ConfigParser {
	private Map<String, Server> servers = new HashMap<>();
	private List<Rule> sendRules = new ArrayList<>();
	private List<Rule> receiveRules = new ArrayList<>();
	private List<String> serverNameList = new ArrayList<>();
	private Map<String, Group> groups = new HashMap<>();
	private List<String> localGroupList = new ArrayList<>();

	private String filename, local_name;
	private List<GroupMessage> messageReceived = new ArrayList<>();
	public String getLocal_name() {
		return local_name;
	}

	private static long CONFIG_FILE_LAST_MODIFIED;
	private File configFile;
	private int processSize;
	
	@SuppressWarnings("unchecked")
	public ConfigParser(String filename, String local_name){
		this.setFilename(filename);
		this.local_name = local_name;
		Yaml yaml = new Yaml();
		InputStream input;
		try {
			configFile = new File(filename);
			CONFIG_FILE_LAST_MODIFIED = configFile.lastModified();
			input = new FileInputStream(configFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		//Map map = (Map) yaml.load(input);
		//Map<String, List<Map<String, Object>>> values = (Map<String, List<Map<String, Object>>>) yaml.load(input);
		Map<String, Object> values = (Map<String,Object>) yaml.load(input);
		
		parseServers((List<Map<String, Object>>)values.get("configuration"), servers);
		parseRules((List<Map<String, Object>>)values.get("sendRules"), sendRules);
		parseRules((List<Map<String, Object>>)values.get("receiveRules"), receiveRules);
		parseGroups((List<Map<String, Object>>)values.get("groups"), groups);
		processSize = (int) values.get("processSize");
		parseLocalGroupList(groups, localGroupList);
		
		
		//System.out.println(servers);
		//System.out.println(sendRules);
		//System.out.println(receiveRules);
		//System.out.println(processSize);
		System.out.println(localGroupList);
	}
	
	public void reconfiguration(){
		Yaml yaml = new Yaml();
		InputStream input;
		try {
			configFile = new File(filename);
			CONFIG_FILE_LAST_MODIFIED = configFile.lastModified();
			input = new FileInputStream(configFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		@SuppressWarnings("unchecked")
		Map<String, List<Map<String, Object>>> values = (Map<String, List<Map<String, Object>>>) yaml.load(input);
		parseRules(values.get("sendRules"), sendRules);
		parseRules(values.get("receiveRules"), receiveRules);
		
		System.out.println("WARNING: CONFIG FILE RELOADED!");
	}
	
	public boolean isUpToDate() {
		if(configFile.lastModified() != CONFIG_FILE_LAST_MODIFIED) {
			return false;
		}
		return true;
	}
	
	public Server getServer(String serverName){
		return servers.get(serverName);
	}
	
	public Group getGroup(String groupName){
		return groups.get(groupName);
	}
	
	public int getIndexOfServer(String serverName){
		return serverNameList.indexOf(serverName);
	}
	
	public List<String> getAllServers(){
		return serverNameList;
	}
	
	/**
	 * Check if the given message matches a rule
	 */
	public Rule matchSendRule(String src, String dest, String kind, int seqNum){
		return matchRule(src, dest, kind, seqNum, sendRules);
	}
	
	public Rule matchReceiveRule(String src, String dest, String kind, int seqNum){
		return matchRule(src, dest, kind, seqNum, receiveRules);
	}
	
	private Rule matchRule(String src, String dest, String kind, int seqNum, List<Rule> rules){
		for(Rule rule: rules){
			if(rule.match(src, dest, kind, seqNum)){
				return rule;
			}
		}
		return null;
	}
	
	/**
	 * Parse the configuration section and get ip ports of all servers
	 * @param aservers
	 * @param servers
	 */
	private void parseServers(List<Map<String, Object>> aservers, Map<String, Server> servers){
		for(Map<String, Object> item: aservers){
			Server server = new Server();
			server.setName((String)item.get("name"));
			server.setIp((String)item.get("ip"));
			server.setPort((Integer)item.get("port"));
			servers.put(server.getName(), server);
			serverNameList.add(server.getName());
		}
	}

	/**
	 * Parse the send/receive rules section 
	 * @param arules
	 * @param rules
	 */
	private void parseRules(List<Map<String, Object>> arules, List<Rule> rules){
		for(Map<String, Object> item: arules){
			Rule rule = new Rule();
			if(item.containsKey("action")){
				rule.setAction((String) item.get("action"));
			}
			if(item.containsKey("src")){
				rule.setSrc((String) item.get("src"));
			}
			if(item.containsKey("dest")){
				rule.setDest((String) item.get("dest"));
			}
			if(item.containsKey("kind")){
				rule.setKind((String) item.get("kind"));
			}
			if(item.containsKey("seqNum")){
				rule.setSeqNum((Integer) item.get("seqNum"));
			}
			rules.add(rule);
		}
	}
	
	private void parseGroups(List<Map<String, Object>> agroups, Map<String, Group> groups){
		for(Map<String, Object> item: agroups){
			String groupName = (String) item.get("name");
			ArrayList<String> groupMember = (ArrayList<String>) item.get("members");
			Group group = new Group(groupName, groupMember, local_name);
			groups.put(groupName, group);
		}
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getProcessSize() {
		return processSize;
	}

	public void setProcessSize(int processSize) {
		this.processSize = processSize;
	}
	
	
	public Boolean checkAndAddReceiveMessage(GroupMessage gmsg){
		if(messageReceived.contains(gmsg)){
			return false;
		}
		messageReceived.add(gmsg);
		//System.out.println(gmsg);
		return true;
	}
	
	/**
	 * Get all the group names that local node belongs to
	 * @param groups
	 * @param grouplist
	 */
	public void parseLocalGroupList(Map<String, Group> groups, List<String> grouplist){
		for(String groupName: groups.keySet()){
			if(groups.get(groupName).getGroupMember().contains(local_name)){
				grouplist.add(groupName);
			}
		}
	}
	
	public List<String> getLocalGroupList() {
		return localGroupList;
	}
}






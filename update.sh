scp -i ~/Desktop/small.pem Configuration.yaml ubuntu@ec2-52-91-211-230.compute-1.amazonaws.com:~/alice
scp -i ~/Desktop/small.pem Configuration.yaml ubuntu@ec2-52-91-211-230.compute-1.amazonaws.com:~/bob
scp -i ~/Desktop/small.pem Configuration.yaml ubuntu@ec2-54-152-89-119.compute-1.amazonaws.com:~/charlie
scp -i ~/Desktop/small.pem Configuration.yaml ubuntu@ec2-54-152-89-119.compute-1.amazonaws.com:~/daphnie

scp -i ~/Desktop/small.pem alice.yaml ubuntu@ec2-52-91-211-230.compute-1.amazonaws.com:~/alice/ApplicationConfig.yaml
scp -i ~/Desktop/small.pem bob.yaml ubuntu@ec2-52-91-211-230.compute-1.amazonaws.com:~/bob/ApplicationConfig.yaml
scp -i ~/Desktop/small.pem charlie.yaml ubuntu@ec2-54-152-89-119.compute-1.amazonaws.com:~/charlie/ApplicationConfig.yaml
scp -i ~/Desktop/small.pem daphnie.yaml ubuntu@ec2-54-152-89-119.compute-1.amazonaws.com:~/daphnie/ApplicationConfig.yaml
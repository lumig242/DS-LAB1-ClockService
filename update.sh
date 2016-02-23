scp -i ~/Desktop/small.pem Configuration.yaml ubuntu@ec2-52-91-211-230.compute-1.amazonaws.com:~/alice
scp -i ~/Desktop/small.pem Configuration.yaml ubuntu@ec2-52-91-211-230.compute-1.amazonaws.com:~/bob
scp -i ~/Desktop/small.pem Configuration.yaml ubuntu@ec2-54-152-89-119.compute-1.amazonaws.com:~/charlie
scp -i ~/Desktop/small.pem Configuration.yaml ubuntu@ec2-54-152-89-119.compute-1.amazonaws.com:~/daphnie
scp -i ~/Desktop/small.pem Configuration.yaml ubuntu@ec2-54-86-212-66.compute-1.amazonaws.com:~/ellis
scp -i ~/Desktop/small.pem Configuration.yaml ubuntu@ec2-54-86-212-66.compute-1.amazonaws.com:~/frank
scp -i ~/Desktop/small.pem Configuration.yaml ubuntu@ec2-52-87-206-141.compute-1.amazonaws.com:~/gavin

scp -i ~/Desktop/small.pem alice.yaml ubuntu@ec2-52-91-211-230.compute-1.amazonaws.com:~/alice/ApplicationConfig.yaml
scp -i ~/Desktop/small.pem bob.yaml ubuntu@ec2-52-91-211-230.compute-1.amazonaws.com:~/bob/ApplicationConfig.yaml
scp -i ~/Desktop/small.pem charlie.yaml ubuntu@ec2-54-152-89-119.compute-1.amazonaws.com:~/charlie/ApplicationConfig.yaml
scp -i ~/Desktop/small.pem daphnie.yaml ubuntu@ec2-54-152-89-119.compute-1.amazonaws.com:~/daphnie/ApplicationConfig.yaml
scp -i ~/Desktop/small.pem ellis.yaml ubuntu@ec2-54-86-212-66.compute-1.amazonaws.com:~/ellis/ApplicationConfig.yaml
scp -i ~/Desktop/small.pem frank.yaml ubuntu@ec2-54-86-212-66.compute-1.amazonaws.com:~/frank/ApplicationConfig.yaml
scp -i ~/Desktop/small.pem gavin.yaml ubuntu@ec2-52-87-206-141.compute-1.amazonaws.com:~/gavin/ApplicationConfig.yaml
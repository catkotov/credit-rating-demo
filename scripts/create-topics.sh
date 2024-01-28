echo "Waiting for Kafka to come online..."
# cub.sh kafka-ready -b kafka:9092 1 20
# create the users topic
kafka-topics.sh --bootstrap-server kafka:9092 --topic users --replication-factor 1 --partitions 4 --create
sleep 24h
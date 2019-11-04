#!/usr/bin/env bash
./gradlew clean && ./gradlew build
spark-submit --class caching.EntryPoint ./build/libs/sparkcaching-1.0-SNAPSHOT.jar --packages com.datastax.spark:spark-cassandra-connector_2.11:2.4.1


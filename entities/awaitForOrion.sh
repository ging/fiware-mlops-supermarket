#!/bin/bash

while true; do
  if curl --fail -s http://orion:1026/version >/dev/null; then
    echo "Connected to Orion"
    break
  else
    echo "Connection to Orion failed. Trying again en 5 seconds..."
    sleep 5
  fi
done
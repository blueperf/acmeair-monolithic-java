#!/usr/bin/env bash

cat << EOF | oc apply -f -
apiVersion: v1
kind: Secret
data:
  username: ${MONGODB_USER}
  password: ${MONGODB_PASS}
metadata:
  name: mongodb-creds
EOF
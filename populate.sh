#!/usr/bin/env bash

ROUTE="$(oc get route.route.openshift.io/mongodb-route -o json | jq -r '.status.ingress[].host')"

curl -k https://${ROUTE}/booking/loader/load
curl -k http://${ROUTE}/flight/loader/load
curl -k http://${ROUTE}/customer/loader/load?numCustomers=10000

printf "${grn}Database Loaded${end}\n"
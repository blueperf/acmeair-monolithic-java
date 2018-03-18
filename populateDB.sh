CLUSTER_NAME=mossdal
eval INGRESS_URL="$(bx cs cluster-get ${CLUSTER_NAME} | grep "Ingress Subdomain" | cut -f 2)"
curl http://${INGRESS_URL}/booking/loader/load || true
printf "\n"
curl http://${INGRESS_URL}/flight/loader/load || true
printf "\n"
curl http://${INGRESS_URL}/customer/loader/load?numCustomers=10000 || true
printf "\n"
printf "${grn}Database Loaded${end}\n"


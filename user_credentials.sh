#!/bin/bash
echo 'Extracting access tokens for users: USAGE: user_credentials.sh r=demo c=demo-app u=<user1,user2...>'

for arg in "$@"
do
	index=$(echo $arg | cut -f1 -d=)
	val=$(echo $arg | cut -f2 -d=)

	case $index in
		r) realm=${val:-demo};;
		c) client_id=${val};;
		u) users=$val;;
		*)
	esac
done

keycloakbaseUrl=http://localhost:8000
client_id=demo-app
users=(user1 user2 test1 superuser)

for user in "${users[@]}"
do
	rm ${user}.json

	curl -v -s -X POST "${keycloakbaseUrl}/auth/realms/${realm}/protocol/openid-connect/token" \
	 --header 'Content-Type: application/x-www-form-urlencoded' \
	 --data-urlencode 'grant_type=password' \
	 --data-urlencode "client_id=${client_id}" \
	 --data-urlencode "username=${user}" --data-urlencode "password=${user}" > ${user}.json
done

echo "finished!"
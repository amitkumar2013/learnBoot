# loop through them
ACCESS_TOKEN=`jq -r .access_token user1.json`

app_baseUrl=localhost:8080
uris=(/ /home /admin /contactus /greet /about-us)

echo "Starting with the API & UI at ${app_baseUrl}"

for uri in "${uris[@]}"
do
  result=$(curl -v -s -H "Authorization: Bearer $ACCESS_TOKEN" -X GET "http://${app_baseUrl}/${uri}")
  if [[ $result == *"Forbidden"* ]]; then
    echo "It's forbidden!"
  else 
    echo $result
  fi
  echo 
  read -t 10 -p "Waiting for 10 seconds ... or hit enter"
done

echo "finished!"
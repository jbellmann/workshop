Instead of using docker-compose find a way to setup every service individually.

##### systemd

```
sudo mkdir -m 0644 -p /lib/systemd/workshop
sudo su # to become root
cd workshop/

nano docker.redis.service

# fill unit file

cd /etc/systemd/system

ln -s /lib/systemd/workshop/docker.redis.service docker.redis.service

exit # to become ubuntu user again
sudo systemctl start docker.redis # can take a while, image has to be fetched

systemctl status docker.redis # to get the status informationen and log output
```

```
...
ln -s /lib/systemd/workshop/docker.postgres.service docker.postgres.service
exit # to become ubuntu user again
sudo systemctl start docker.postgres

systemctl status docker.postgres.service


sudo apt-get instsall postgresql-client

psql -h localhost -p 5432 -U postgres -d postgres

postgres=# \l     #list all databases
```

```
journalctl -u docker.ehrbase-compose.service -n 20 -f
```


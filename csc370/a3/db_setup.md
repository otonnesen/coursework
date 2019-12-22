# Setup
- Create user 370admin
- Set password to 'password'
- Create postgres server
- Add role '370admin' to server
- Add db 'db' to server

# To connect manually
`su 370admin`
Enter password

`psql -h localhost -p 5432 -d db`
Enter password

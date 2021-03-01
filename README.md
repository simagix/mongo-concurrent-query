# Concurrent Queries Trick

## Setup

Spin up a MongoDB standalone server and create a login user with "password" as its password.  Use [*Keyhole*](https://github.com/simagix/keyhole) to populate data.

```bash
export MONGO_URI="mongodb://user:password@localhost/keyhole?authSource=admin"
keyhole -seed ${MONGO_URI}
```

Each document of *keyhole.vehicles* has a filed *_batch* with a possible value from 0 to 3.

## Execute Query `{ "color": "Red" }`

Execute the command below:

```bash
gradle run
```

The application executes the query of `{ "color": "Red" }` using 4 threads:
- `{ "color": "Red", "_batch": 0 }`
- `{ "color": "Red", "_batch": 1 }`
- `{ "color": "Red", "_batch": 2 }`
- `{ "color": "Red", "_batch": 3 }`
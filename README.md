![ArangoDB-Logo](https://docs.arangodb.com/assets/arangodb_logo_2016_inverted.png)

arangodb object mapper
=========================

A simple object mapper (based on [Jackson](http://wiki.fasterxml.com/JacksonHome)) to store java objects into a ArangoDB database.


Installation & Testing
=======================

Please check the
[ArangoDB Documentation](https://www.arangodb.com/documentation/)
for installation and compilation instructions.

Start ArangoDB on localhost port 8529.

The arangodb object mapper is built with
	```mvn clean install```

First Steps
=======================

Create a new class
```
public class Point extends ArangoDbDocument {
    private Integer x;
    private Integer y;
    public Point() {
    }
    public Point(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }    
    public Integer getX() {
        return x;
    }
    public void setX(Integer x) {
        this.x = x;
    }
    public Integer getY() {
        return y;
    }
    public void setY(Integer y) {
        this.y = y;
    }
}
```

Create a new repository class
```
class PointRepository extends ArangoDbRepository<Point> {        
    public PointRepository(Database database) {
        super(database, Point.class);
    }
```

Create database connection and repository
```
ArangoDbHttpClient client = new ArangoDbHttpClient.Builder().host("localhost")
                                                            .port(8529)
                                                            .build();       

/* use "_system" database. can use any other database if required */
Database database = new Database(client, "_system");

PointRepository repo = new PointRepository(database);
```

Save a object
```
Point p = new Point(1, 2);        
try {
    repo.create(p);
} catch (ArangoDb4JException e) {
    ...
}
```

Update the object
```
p.setX(5);        
try {
    repo.update(p);
} catch (ArangoDb4JException e) {
    ...
}
```

Get all Objects
```
Cursor<Point> cursor = null;
try {
    cursor = repo.getAll();
} catch (ArangoDb4JException e) {
    ...
}
while (cursor.hasNext()) {
    Point p = cursor.next();
    ...
}
```

Sort by key
```
Cursor<Point> cursor = repo.getQuery().sort("x", PropertySort.Direction.DESCENDING).execute();
while (cursor.hasNext()) {
	Point p = cursor.next();
	...
}
```

Windowing
```
Cursor<Point> cursor = repo.getQuery().limit(11,10).sort("x", PropertySort.Direction.DESCENDING).execute();
while (cursor.hasNext()) {
	Point p = cursor.next();
	...
}
```

Creating databases and collections
```
ArangoDbHttpClient client = new ArangoDbHttpClient.Builder().host("localhost")
                                                            .port(8529)
                                                            .build();      
Database db = Database.createDatabase(client, "MyDatabase"); 
Collection coll = db.createCollection("MyCollection");

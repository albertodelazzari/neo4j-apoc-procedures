package apoc.convert;

import static apoc.util.MapUtil.map;
import static apoc.util.TestUtil.testCall;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.test.TestGraphDatabaseFactory;

import apoc.util.TestUtil;

/**
 * @author mh
 * @since 29.05.16
 */
public class ConvertTest {

    private static GraphDatabaseService db;

    @BeforeClass
    public static void setUp() throws Exception {
        db = new TestGraphDatabaseFactory().newImpermanentDatabase();
        TestUtil.registerProcedure(db, Convert.class);
    }

    @AfterClass
    public static void tearDown() {
        db.shutdown();
    }

    @Test
    public void testToMap() throws Exception {
        testCall(db, "return apoc.convert.toMap({a}) as value", map("a", map("a", "b")), r -> assertEquals(map("a", "b"), r.get("value")));
        testCall(db, "return apoc.convert.toMap({a}) as value", map("a", null), r -> assertEquals(null, r.get("value")));
        final Map<String, Object> props = map("name", "John", "age", 39);
        testCall(db, "create (n) set n=$props return apoc.convert.toMap(n) as value", map("props", props), r -> assertEquals(props, r.get("value")));
    }

    @Test
    public void testToList() throws Exception {
        testCall(db, "return apoc.convert.toList({a}) as value", map("a", null), r -> assertEquals(null, r.get("value")));
        testCall(db, "return apoc.convert.toList({a}) as value", map("a", new Object[]{"a"}), r -> assertEquals(singletonList("a"), r.get("value")));
        testCall(db, "return apoc.convert.toList({a}) as value", map("a", singleton("a")), r -> assertEquals(singletonList("a"), r.get("value")));
        testCall(db, "return apoc.convert.toList({a}) as value", map("a", singletonList("a")), r -> assertEquals(singletonList("a"), r.get("value")));
        testCall(db, "return apoc.convert.toList({a}) as value", map("a", singletonList("a").iterator()), r -> assertEquals(singletonList("a"), r.get("value")));
    }

    @Test
    public void testToBoolean() throws Exception {
        testCall(db, "return apoc.convert.toBoolean('true') as value", r -> assertEquals(true, r.get("value")));
        testCall(db, "return apoc.convert.toBoolean(1) as value", r -> assertEquals(true, r.get("value")));
        testCall(db, "return apoc.convert.toBoolean('yes') as value", r -> assertEquals(true, r.get("value")));
        testCall(db, "return apoc.convert.toBoolean('false') as value", r -> assertEquals(false, r.get("value")));
        testCall(db, "return apoc.convert.toBoolean(0) as value", r -> assertEquals(false, r.get("value")));
        testCall(db, "return apoc.convert.toBoolean('no') as value", r -> assertEquals(false, r.get("value")));
        testCall(db, "return apoc.convert.toBoolean('') as value", r -> assertEquals(false, r.get("value")));
        testCall(db, "return apoc.convert.toBoolean(null) as value", r -> assertEquals(false, r.get("value")));
    }

    @Test
    public void testToString() throws Exception {
        testCall(db, "return apoc.convert.toString(null) as value", r -> assertEquals(null, r.get("value")));
        testCall(db, "return apoc.convert.toString('a') as value", r -> assertEquals("a", r.get("value")));
        testCall(db, "return apoc.convert.toString(1) as value", r -> assertEquals("1", r.get("value")));
        testCall(db, "return apoc.convert.toString(true) as value", r -> assertEquals("true", r.get("value")));
        testCall(db, "return apoc.convert.toString([1]) as value", r -> assertEquals("[1]", r.get("value")));
    }

    @Test
    public void testToNode() throws Exception {
        testCall(db, "CREATE (n) WITH [n] as x RETURN apoc.convert.toNode(x[0]) as node",
                r -> assertEquals(true, r.get("node") instanceof Node));
        testCall(db, "RETURN apoc.convert.toNode(null) AS node", r -> assertEquals(null, r.get("node")));
    }

    @Test
    public void testToRelationship() throws Exception {
        testCall(db, "CREATE (n)-[r:KNOWS]->(m) WITH [r] as x RETURN apoc.convert.toRelationship(x[0]) AS rel",
                r -> assertEquals(true, r.get("rel") instanceof Relationship));
        testCall(db, "RETURN apoc.convert.toRelationship(null) AS rel", r -> assertEquals(null, r.get("rel")));
    }

    @Test
    public void testToSet() throws Exception {
        testCall(db, "return apoc.convert.toSet({a}) as value", map("a", null), r -> assertEquals(null, r.get("value")));
        testCall(db, "return apoc.convert.toSet({a}) as value", map("a", new Object[]{"a"}), r -> assertEquals(singletonList("a"), r.get("value")));
        testCall(db, "return apoc.convert.toSet({a}) as value", map("a", singleton("a")), r -> assertEquals(singletonList("a"), r.get("value")));
        testCall(db, "return apoc.convert.toSet({a}) as value", map("a", singletonList("a")), r -> assertEquals(singletonList("a"), r.get("value")));
        testCall(db, "return apoc.convert.toSet({a}) as value", map("a", singletonList("a").iterator()), r -> assertEquals(singletonList("a"), r.get("value")));
    }

    @Test
    public void testToStringList() throws Exception {
         testCall(db, "return apoc.convert.toStringList([null, 'a', 1, true, [1]]) as value",
        		 r -> assertEquals(Arrays.asList(null, "a", "1", "true", "[1]"), r.get("value")));
    }

    @Test
    public void testToIntList() throws Exception {
         testCall(db, "return apoc.convert.toIntList([null, 1, '1']) as value",
        		 r -> assertEquals(Arrays.asList(null, 1L, 1L), r.get("value")));
    }

    @Test
    public void testToBooleanList() throws Exception {
    	testCall(db, "return apoc.convert.toBooleanList(['false', 0, 'no', '', null]) as value",
    			r -> assertEquals(Arrays.asList(false, false, false, false, false), r.get("value")));
		testCall(db, "return apoc.convert.toBooleanList(['true', 1, 'yes']) as value",
				r -> assertEquals(Arrays.asList(true, true, true), r.get("value")));
    }

    @Test
    public void testToNodeList() throws Exception {
        testCall(db, "CREATE (n) WITH [n] as x RETURN apoc.convert.toNodeList(x) as nodes",
                r -> {
					assertEquals(true, r.get("nodes") instanceof List);
					List<Node> nodes = (List<Node>) r.get("nodes");
					assertEquals(true, nodes.get(0) instanceof Node);
                });
    }

    @Test
    public void testToRelationshipList() throws Exception {
        testCall(db, "CREATE (n)-[r:KNOWS]->(m) WITH [r] as x  RETURN apoc.convert.toRelationshipList(x) as rels",
                r -> {
					assertEquals(true, r.get("rels") instanceof List);
					List<Relationship> rels = (List<Relationship>) r.get("rels");
					assertEquals(true, rels.get(0) instanceof Relationship);
                });
    }

}

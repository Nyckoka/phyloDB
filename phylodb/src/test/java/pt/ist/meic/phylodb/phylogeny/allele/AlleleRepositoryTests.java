package pt.ist.meic.phylodb.phylogeny.allele;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.neo4j.ogm.model.Result;
import pt.ist.meic.phylodb.RepositoryTestsContext;
import pt.ist.meic.phylodb.phylogeny.allele.model.Allele;
import pt.ist.meic.phylodb.utils.db.EntityRepository;
import pt.ist.meic.phylodb.utils.db.Query;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

public class AlleleRepositoryTests extends RepositoryTestsContext {

	private static final int LIMIT = 2;
	private static final UUID PROJECTID = PROJECT1.getPrimaryKey();
	private static final Allele[] STATE = new Allele[]{ALLELE11, ALLELE12, ALLELE11P, ALLELE12P};

	private static Stream<Arguments> findAllNoProject_params() {
		String id1 = "3test", id3 = "5test";
		String taxonKey = TAXON1.getPrimaryKey();
		String locusKey = LOCUS1.getPrimaryKey().getId();
		Allele first = new Allele(taxonKey, locusKey, id1, 1, false, "description", null),
				firstChanged = new Allele(taxonKey, locusKey, id1, 2, false, "description2", null),
				second = new Allele(taxonKey, locusKey, "4test", 1, false, null, null),
				third = new Allele(taxonKey, locusKey, id3, 1, false, "description3", null),
				thirdChanged = new Allele(taxonKey, locusKey, id3, 2, false, null, null),
				fourth = new Allele(taxonKey, locusKey, "6test", 1, false, null, null);
		return Stream.of(Arguments.of(0, new Allele[0], new Allele[0]),
				Arguments.of(0, new Allele[]{STATE[0]}, new Allele[]{STATE[0]}),
				Arguments.of(0, new Allele[]{first, firstChanged}, new Allele[]{firstChanged}),
				Arguments.of(0, new Allele[]{STATE[0], STATE[1], first}, new Allele[]{STATE[0], STATE[1]}),
				Arguments.of(0, new Allele[]{STATE[0], STATE[1], first, firstChanged}, new Allele[]{STATE[0], STATE[1]}),
				Arguments.of(1, new Allele[0], new Allele[0]),
				Arguments.of(1, new Allele[]{STATE[0]}, new Allele[0]),
				Arguments.of(1, new Allele[]{first, firstChanged}, new Allele[0]),
				Arguments.of(1, new Allele[]{STATE[0], STATE[1], first}, new Allele[]{first}),
				Arguments.of(1, new Allele[]{STATE[0], STATE[1], first, firstChanged}, new Allele[]{firstChanged}),
				Arguments.of(1, new Allele[]{STATE[0], STATE[1], first, second}, new Allele[]{first, second}),
				Arguments.of(1, new Allele[]{STATE[0], STATE[1], first, firstChanged, second}, new Allele[]{firstChanged, second}),
				Arguments.of(1, new Allele[]{STATE[0], STATE[1], first, firstChanged}, new Allele[]{firstChanged}),
				Arguments.of(2, new Allele[0], new Allele[0]),
				Arguments.of(2, new Allele[]{STATE[0]}, new Allele[0]),
				Arguments.of(2, new Allele[]{first, firstChanged}, new Allele[0]),
				Arguments.of(2, new Allele[]{STATE[0], STATE[1], first, second, third}, new Allele[]{third}),
				Arguments.of(2, new Allele[]{STATE[0], STATE[1], first, second, third, thirdChanged}, new Allele[]{thirdChanged}),
				Arguments.of(2, new Allele[]{STATE[0], STATE[1], first, second, third, fourth}, new Allele[]{third, fourth}),
				Arguments.of(2, new Allele[]{STATE[0], STATE[1], first, second, third, thirdChanged, fourth}, new Allele[]{thirdChanged, fourth}),
				Arguments.of(-1, new Allele[0], new Allele[0]));
	}

	private static Stream<Arguments> findAllProject_params() {
		String id1 = "3test", id3 = "5test";
		String taxonKey = TAXON1.getPrimaryKey();
		String locusKey = LOCUS1.getPrimaryKey().getId();
		Allele first = new Allele(taxonKey, locusKey, id1, 1, false, "description", PROJECTID),
				firstChanged = new Allele(taxonKey, locusKey, id1, 2, false, "description2", PROJECTID),
				second = new Allele(taxonKey, locusKey, "4test", 1, false, null, PROJECTID),
				third = new Allele(taxonKey, locusKey, id3, 1, false, "description3", PROJECTID),
				thirdChanged = new Allele(taxonKey, locusKey, id3, 2, false, null, PROJECTID),
				fourth = new Allele(taxonKey, locusKey, "6test", 1, false, null, PROJECTID);
		return Stream.of(Arguments.of(0, new Allele[0], new Allele[0]),
				Arguments.of(0, new Allele[]{STATE[2]}, new Allele[]{STATE[2]}),
				Arguments.of(0, new Allele[]{first, firstChanged}, new Allele[]{firstChanged}),
				Arguments.of(0, new Allele[]{STATE[2], STATE[3], first}, new Allele[]{STATE[2], STATE[3]}),
				Arguments.of(0, new Allele[]{STATE[2], STATE[3], first, firstChanged}, new Allele[]{STATE[2], STATE[3]}),
				Arguments.of(1, new Allele[0], new Allele[0]),
				Arguments.of(1, new Allele[]{STATE[2]}, new Allele[0]),
				Arguments.of(1, new Allele[]{first, firstChanged}, new Allele[0]),
				Arguments.of(1, new Allele[]{STATE[2], STATE[3], first}, new Allele[]{first}),
				Arguments.of(1, new Allele[]{STATE[2], STATE[3], first, firstChanged}, new Allele[]{firstChanged}),
				Arguments.of(1, new Allele[]{STATE[2], STATE[3], first, second}, new Allele[]{first, second}),
				Arguments.of(1, new Allele[]{STATE[2], STATE[3], first, firstChanged, second}, new Allele[]{firstChanged, second}),
				Arguments.of(1, new Allele[]{STATE[2], STATE[3], first, firstChanged}, new Allele[]{firstChanged}),
				Arguments.of(2, new Allele[0], new Allele[0]),
				Arguments.of(2, new Allele[]{STATE[2]}, new Allele[0]),
				Arguments.of(2, new Allele[]{first, firstChanged}, new Allele[0]),
				Arguments.of(2, new Allele[]{STATE[2], STATE[3], first, second, third}, new Allele[]{third}),
				Arguments.of(2, new Allele[]{STATE[2], STATE[3], first, second, third, thirdChanged}, new Allele[]{thirdChanged}),
				Arguments.of(2, new Allele[]{STATE[2], STATE[3], first, second, third, fourth}, new Allele[]{third, fourth}),
				Arguments.of(2, new Allele[]{STATE[2], STATE[3], first, second, third, thirdChanged, fourth}, new Allele[]{thirdChanged, fourth}),
				Arguments.of(-1, new Allele[0], new Allele[0]));
	}

	private static Stream<Arguments> find_params() {
		Allele.PrimaryKey key = new Allele.PrimaryKey(TAXON1.getPrimaryKey(), LOCUS1.getPrimaryKey().getId(), "test"),
				keyP = new Allele.PrimaryKey(TAXON1.getPrimaryKey(), LOCUS1.getPrimaryKey().getId(), "test", PROJECTID);
		Allele first = new Allele(key.getTaxonId(), key.getLocusId(), key.getId(), 1, false, null, key.getProjectId()),
				second = new Allele(key.getTaxonId(), key.getLocusId(), key.getId(), 2, false, null, key.getProjectId()),
				firstP = new Allele(keyP.getTaxonId(), keyP.getLocusId(), keyP.getId(), 1, false, null, keyP.getProjectId()),
				secondP = new Allele(keyP.getTaxonId(), keyP.getLocusId(), keyP.getId(), 2, false, null, keyP.getProjectId());
		return Stream.of(Arguments.of(key, 1, new Allele[0], null),
				Arguments.of(key, 1, new Allele[]{first}, first),
				Arguments.of(key, 2, new Allele[]{first, second}, second),
				Arguments.of(key, -3, new Allele[0], null),
				Arguments.of(key, 10, new Allele[]{first}, null),
				Arguments.of(key, -10, new Allele[]{first, second}, null),
				Arguments.of(keyP, 1, new Allele[0], null),
				Arguments.of(keyP, 1, new Allele[]{firstP}, firstP),
				Arguments.of(keyP, 2, new Allele[]{firstP, secondP}, secondP),
				Arguments.of(keyP, -3, new Allele[0], null),
				Arguments.of(keyP, 10, new Allele[]{firstP}, null),
				Arguments.of(keyP, -10, new Allele[]{firstP, secondP}, null),
				Arguments.of(null, 1, new Allele[0], null));
	}

	private static Stream<Arguments> exists_params() {
		Allele.PrimaryKey key = new Allele.PrimaryKey(TAXON1.getPrimaryKey(), LOCUS1.getPrimaryKey().getId(), "test"),
				keyP = new Allele.PrimaryKey(TAXON1.getPrimaryKey(), LOCUS1.getPrimaryKey().getId(), "test", PROJECTID);
		Allele first = new Allele(key.getTaxonId(), key.getLocusId(), key.getId(), 1, false, null, key.getProjectId()),
				second = new Allele(key.getTaxonId(), key.getLocusId(), key.getId(), 1, true, null, key.getProjectId()),
				firstP = new Allele(keyP.getTaxonId(), keyP.getLocusId(), keyP.getId(), 1, false, null, keyP.getProjectId()),
				secondP = new Allele(keyP.getTaxonId(), keyP.getLocusId(), keyP.getId(), 1, true, null, keyP.getProjectId());
		return Stream.of(Arguments.of(key, new Allele[0], false),
				Arguments.of(key, new Allele[]{first}, true),
				Arguments.of(key, new Allele[]{second}, false),
				Arguments.of(keyP, new Allele[0], false),
				Arguments.of(keyP, new Allele[]{firstP}, true),
				Arguments.of(keyP, new Allele[]{secondP}, false),
				Arguments.of(null, new Allele[0], false));
	}

	private static Stream<Arguments> save_params() {
		Allele.PrimaryKey key = new Allele.PrimaryKey(TAXON1.getPrimaryKey(), LOCUS1.getPrimaryKey().getId(), "3"),
				keyP = new Allele.PrimaryKey(TAXON1.getPrimaryKey(), LOCUS1.getPrimaryKey().getId(), "3", PROJECTID);
		Allele first = new Allele(key.getTaxonId(), key.getLocusId(), key.getId(), 1, false, null, key.getProjectId()),
				second = new Allele(key.getTaxonId(), key.getLocusId(), key.getId(), 2, false, null, key.getProjectId()),
				firstP = new Allele(keyP.getTaxonId(), keyP.getLocusId(), keyP.getId(), 1, false, null, keyP.getProjectId()),
				secondP = new Allele(keyP.getTaxonId(), keyP.getLocusId(), keyP.getId(), 2, false, null, keyP.getProjectId());
		return Stream.of(Arguments.of(first, new Allele[0], null, new Allele[]{STATE[0], STATE[1], first}, true, 2, 2),
				Arguments.of(second, new Allele[]{first}, null, new Allele[]{STATE[0], STATE[1], first, second}, true, 1, 1),
				Arguments.of(firstP, new Allele[0], PROJECTID, new Allele[]{STATE[2], STATE[3], firstP}, true, 2, 3),
				Arguments.of(secondP, new Allele[]{firstP}, PROJECTID, new Allele[]{STATE[2], STATE[3], firstP, secondP}, true, 1, 1),
				Arguments.of(null, new Allele[0], null, new Allele[]{STATE[0], STATE[1]}, false, 0, 0),
				Arguments.of(null, new Allele[0], PROJECTID, new Allele[]{STATE[2], STATE[3]}, false, 0, 0));
	}

	private static Stream<Arguments> remove_params() {
		Allele.PrimaryKey key = new Allele.PrimaryKey(TAXON1.getPrimaryKey(), LOCUS1.getPrimaryKey().getId(), "3"),
				keyP = new Allele.PrimaryKey(TAXON1.getPrimaryKey(), LOCUS1.getPrimaryKey().getId(), "3", PROJECTID);
		Allele first = new Allele(key.getTaxonId(), key.getLocusId(), key.getId(), 1, false, null, key.getProjectId()),
				second = new Allele(key.getTaxonId(), key.getLocusId(), key.getId(), 1, true, null, key.getProjectId()),
				firstP = new Allele(keyP.getTaxonId(), keyP.getLocusId(), keyP.getId(), 1, false, null, keyP.getProjectId()),
				secondP = new Allele(keyP.getTaxonId(), keyP.getLocusId(), keyP.getId(), 1, true, null, keyP.getProjectId());
		return Stream.of(Arguments.of(key, new Allele[0], null, new Allele[]{STATE[0], STATE[1]}, false),
				Arguments.of(key, new Allele[]{first}, null, new Allele[]{STATE[0], STATE[1], second}, true),
				Arguments.of(null, new Allele[0], null, new Allele[]{STATE[0], STATE[1]}, false),
				Arguments.of(keyP, new Allele[0], PROJECTID, new Allele[]{STATE[2], STATE[3]}, false),
				Arguments.of(keyP, new Allele[]{firstP}, PROJECTID, new Allele[]{STATE[2], STATE[3], secondP}, true),
				Arguments.of(null, new Allele[0], PROJECTID, new Allele[]{STATE[2], STATE[3]}, false));
	}

	private static Stream<Arguments> anyMissing_params() {
		List<Entity<Allele.PrimaryKey>> references1 = new ArrayList<>(), references2 = new ArrayList<>(), references3 = new ArrayList<>(), references4 = new ArrayList<>(),
				references5 = new ArrayList<>(), references6 = new ArrayList<>(), references7 = new ArrayList<>();
		Entity<Allele.PrimaryKey> reference1 = new Entity<>(new Allele.PrimaryKey(TAXON1.getPrimaryKey(), LOCUS1.getPrimaryKey().getId(), STATE[0].getPrimaryKey().getId()), EntityRepository.CURRENT_VERSION_VALUE, false),
				reference2 = new Entity<>(new Allele.PrimaryKey(TAXON1.getPrimaryKey(), LOCUS1.getPrimaryKey().getId(), STATE[2].getPrimaryKey().getId(), PROJECTID), EntityRepository.CURRENT_VERSION_VALUE, false),
				reference3 = new Entity<>(new Allele.PrimaryKey(TAXON1.getPrimaryKey(), LOCUS1.getPrimaryKey().getId(), STATE[1].getPrimaryKey().getId()), EntityRepository.CURRENT_VERSION_VALUE, false),
				reference4 = new Entity<>(new Allele.PrimaryKey(TAXON1.getPrimaryKey(), LOCUS1.getPrimaryKey().getId(), STATE[3].getPrimaryKey().getId(), PROJECTID), EntityRepository.CURRENT_VERSION_VALUE, false),
				notReference1 = new Entity<>(new Allele.PrimaryKey(TAXON1.getPrimaryKey(), LOCUS1.getPrimaryKey().getId(), "not"), EntityRepository.CURRENT_VERSION_VALUE, false),
				notReference2 = new Entity<>(new Allele.PrimaryKey(TAXON1.getPrimaryKey(), LOCUS1.getPrimaryKey().getId(), "not", PROJECTID), EntityRepository.CURRENT_VERSION_VALUE, false);
		references1.add(reference1);
		references2.add(reference2);
		references3.add(notReference1);
		references4.add(notReference2);
		references5.add(reference2);
		references5.add(reference4);
		references6.add(reference1);
		references6.add(reference3);
		references6.add(null);
		references7.add(notReference1);
		references7.add(reference2);
		references7.add(null);
		return Stream.of(Arguments.of(references1, false),
				Arguments.of(references2, false),
				Arguments.of(references3, true),
				Arguments.of(references4, true),
				Arguments.of(references5, false),
				Arguments.of(references6, false),
				Arguments.of(references4, true),
				Arguments.of(references7, true));
	}

	private static Stream<Arguments> saveAll_params() {
		Allele.PrimaryKey firstKey = ALLELE11.getPrimaryKey(), firstPkey = ALLELE11P.getPrimaryKey();
		Allele firstConflict = new Allele(firstKey.getTaxonId(), firstKey.getLocusId(), firstKey.getId(), 2, false, "teste", firstKey.getProjectId()),
				firstPConflict = new Allele(firstPkey.getTaxonId(), firstPkey.getLocusId(), firstPkey.getId(), 2, false, "sequencep", firstPkey.getProjectId());
		return Stream.of(Arguments.of(Collections.emptyList(), new Allele[]{STATE[0], STATE[1]}, null, new Allele[]{STATE[0], STATE[1]}, false, 0, 0),
				Arguments.of(Collections.singletonList(STATE[0]), new Allele[]{STATE[1]}, null, new Allele[]{STATE[0], STATE[1]}, true, 2, 2),
				Arguments.of(Collections.singletonList(firstConflict), new Allele[]{STATE[0]}, null, new Allele[]{STATE[0], firstConflict}, true, 1, 1),
				Arguments.of(Collections.singletonList(STATE[2]), new Allele[]{STATE[3]}, PROJECTID, new Allele[]{STATE[2], STATE[3]}, true, 2, 3),
				Arguments.of(Collections.singletonList(firstPConflict), new Allele[]{STATE[2], STATE[3]}, PROJECTID, new Allele[]{STATE[2], firstPConflict, STATE[3]}, true, 1, 1),
				Arguments.of(Arrays.asList(STATE[0], STATE[1]), new Allele[0], null, new Allele[]{STATE[0], STATE[1]}, true, 4, 4),
				Arguments.of(Arrays.asList(firstConflict, STATE[1]), new Allele[]{STATE[0]}, null, new Allele[]{STATE[0], firstConflict, STATE[1]}, true, 3, 3),
				Arguments.of(Arrays.asList(STATE[2], STATE[3]), new Allele[0], PROJECTID, new Allele[]{STATE[2], STATE[3]}, true, 4, 6),
				Arguments.of(Arrays.asList(firstPConflict, STATE[3]), new Allele[]{STATE[2]}, PROJECTID, new Allele[]{STATE[2], firstPConflict, STATE[3]}, true, 3, 4));
	}

	private void store(Allele[] alleles) {
		for (Allele allele : alleles) {
			Object[] params = new Object[]{allele.getTaxonId(), allele.getLocusId()};
			String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})\n" +
					"WHERE t.deprecated = false AND l.deprecated = false\n";
			String project = "";
			if (allele.getPrimaryKey().getProjectId() != null) {
				params = new Object[]{allele.getTaxonId(), allele.getLocusId(), allele.getPrimaryKey().getProjectId()};
				statement += "MATCH(p:Project {id: $}) WHERE p.deprecated = false WITH t, l, p\n";
				project = "<-[:CONTAINS]-(p)";
			}
			Query query = new Query(statement, params);
			String statement2 = "MERGE (l)-[:CONTAINS]->(a:Allele {id: $})" + project + " SET a.deprecated = $ WITH l, a\n" +
					"OPTIONAL MATCH (a)-[r:CONTAINS_DETAILS]->(ad:AlleleDetails)\n" +
					"WHERE NOT EXISTS(r.to) SET r.to = datetime()\n" +
					"WITH l, a, COALESCE(MAX(r.version), 0) + 1 as v\n" +
					"CREATE (a)-[:CONTAINS_DETAILS {from: datetime(), version: v}]->(ad:AlleleDetails {sequence: $}) ";
			query.appendQuery(statement2).addParameter(allele.getPrimaryKey().getId(), allele.isDeprecated(), allele.getSequence());
			execute(query);
		}
	}

	private Allele parse(Map<String, Object> row) {
		UUID project = row.get("project") != null ? UUID.fromString((String) row.get("project")) : null;
		return new Allele((String) row.get("taxonId"),
				(String) row.get("locusId"),
				(String) row.get("id"),
				(long) row.get("version"),
				(boolean) row.get("deprecated"),
				(String) row.get("sequence"),
				project);
	}

	private Allele[] findAll(UUID projectId) {
		String statement = "MATCH (t:Taxon {id: $})-[:CONTAINS]->(l:Locus {id: $})-[:CONTAINS]->(a:Allele)-[r:CONTAINS_DETAILS]->(ad:AlleleDetails)\n";
		List<Object> params = new ArrayList<>();
		params.add(TAXON1.getPrimaryKey());
		params.add(LOCUS1.getPrimaryKey().getId());
		if (projectId != null) {
			params.add(projectId);
			statement += "\nMATCH (a)<-[:CONTAINS]-(p:Project {id: $}) WHERE p.deprecated = false\n" +
					"RETURN t.id as taxonId, l.id as locusId, a.id as id, a.deprecated as deprecated, r.version as version, ad.sequence as sequence, p.id as project\n";
		} else {
			statement += "WHERE NOT (a)<-[:CONTAINS]-(:Project)\n" +
					"\nRETURN t.id as taxonId, l.id as locusId, a.id as id, a.deprecated as deprecated, r.version as version, ad.sequence as sequence\n";
		}
		statement += "ORDER BY t.id, l.id, a.id, version";
		Result result = query(new Query(statement, params.toArray()));
		if (result == null) return new Allele[0];
		return StreamSupport.stream(result.spliterator(), false)
				.map(this::parse)
				.toArray(Allele[]::new);
	}

	@BeforeEach
	public void init() {
		taxonRepository.save(TAXON1);
		locusRepository.save(LOCUS1);
		projectRepository.save(PROJECT1);
	}

	@ParameterizedTest
	@MethodSource("findAllNoProject_params")
	public void findAllNoProject(int page, Allele[] state, Allele[] expected) {
		store(state);
		Optional<List<Allele>> result = alleleRepository.findAll(page, LIMIT, TAXON1.getPrimaryKey(), LOCUS1.getPrimaryKey().getId(), null);
		if (expected.length == 0 && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertTrue(result.isPresent());
		List<Allele> alleles = result.get();
		assertEquals(expected.length, alleles.size());
		assertArrayEquals(expected, alleles.toArray());
	}

	@ParameterizedTest
	@MethodSource("findAllProject_params")
	public void findAllProject(int page, Allele[] state, Allele[] expected) {
		store(state);
		Optional<List<Allele>> result = alleleRepository.findAll(page, LIMIT, TAXON1.getPrimaryKey(), LOCUS1.getPrimaryKey().getId(), PROJECTID);
		if (expected.length == 0 && !result.isPresent()) {
			assertTrue(true);
			return;
		}
		assertTrue(result.isPresent());
		List<Allele> alleles = result.get();
		assertEquals(expected.length, alleles.size());
		assertArrayEquals(expected, alleles.toArray());
	}

	@ParameterizedTest
	@MethodSource("find_params")
	public void find(Allele.PrimaryKey key, long version, Allele[] state, Allele expected) {
		store(AlleleRepositoryTests.STATE);
		store(state);
		Optional<Allele> result = alleleRepository.find(key, version);
		assertTrue((expected == null && !result.isPresent()) || (expected != null && result.isPresent()));
		if (expected != null)
			assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource("exists_params")
	public void exists(Allele.PrimaryKey key, Allele[] state, boolean expected) {
		store(AlleleRepositoryTests.STATE);
		store(state);
		boolean result = alleleRepository.exists(key);
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("save_params")
	public void save(Allele allele, Allele[] state, UUID projectId, Allele[] expectedState, boolean executed, int nodesCreated, int relationshipsCreated) {
		store(AlleleRepositoryTests.STATE);
		store(state);
		int nodes = countNodes();
		int relationships = countRelationships();
		boolean result = alleleRepository.save(allele);
		Allele[] stateResult = findAll(projectId);
		if (executed) {
			assertTrue(result);
			assertEquals(nodes + nodesCreated, countNodes());
			assertEquals(relationships + relationshipsCreated, countRelationships());
		} else
			assertFalse(result);
		assertArrayEquals(expectedState, stateResult);
	}

	@ParameterizedTest
	@MethodSource("remove_params")
	public void remove(Allele.PrimaryKey key, Allele[] state, UUID projectId, Allele[] expectedState, boolean expectedResult) {
		store(AlleleRepositoryTests.STATE);
		store(state);
		boolean result = alleleRepository.remove(key);
		Allele[] stateResult = findAll(projectId);
		assertEquals(expectedResult, result);
		assertArrayEquals(expectedState, stateResult);
	}

	@ParameterizedTest
	@MethodSource("anyMissing_params")
	public void anyMissing(List<Entity<Allele.PrimaryKey>> references, boolean expected) {
		store(AlleleRepositoryTests.STATE);
		boolean result = alleleRepository.anyMissing(references);
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@MethodSource("saveAll_params")
	public void saveAll(List<Allele> alleles, Allele[] state, UUID projectId, Allele[] expectedState, boolean executed, int nodesCreated, int relationshipsCreated) {
		store(state);
		int nodes = countNodes();
		int relationships = countRelationships();
		boolean result = alleleRepository.saveAll(alleles);
		if (executed) {
			assertTrue(result);
			assertEquals(nodes + nodesCreated, countNodes());
			assertEquals(relationships + relationshipsCreated, countRelationships());
		} else
			assertFalse(result);
		Allele[] stateResult = findAll(projectId);
		assertArrayEquals(expectedState, stateResult);
	}

}

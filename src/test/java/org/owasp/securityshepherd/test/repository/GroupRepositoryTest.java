package org.owasp.securityshepherd.test.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.Group;
import org.owasp.securityshepherd.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class GroupRepositoryTest {

	@Autowired
	private GroupRepository groupRepository;

	@Test
	public void existsById_ExistingId_ReturnsTrue() {

		Group existsByIdExistingIdGroup = Group.builder().build();

		Group returnedGroup = groupRepository.save(existsByIdExistingIdGroup);

		assertNotNull(returnedGroup.getId());
		assertTrue(groupRepository.existsById(returnedGroup.getId()));

	}

	@Test
	public void existsById_NonExistentId_ReturnsFalse() {

		assertFalse(groupRepository.existsById(1234567890L));

	}

	@Test
	public void existsByName_ExistingName_ReturnsTrue() {

		Group existsByNameExistingNameGroup = Group.builder().name("existsByName_ExistingName").build();

		assertFalse(groupRepository.existsByName("existsByName_ExistingName"));

		groupRepository.save(existsByNameExistingNameGroup);

		assertTrue(groupRepository.existsByName("existsByName_ExistingName"));

		Group existsByNameExistingNameLongerNameGroup = Group.builder().name("existsByName_ExistingName_LongerName")
				.build();

		assertFalse(groupRepository.existsByName("existsByName_ExistingName_LongerName"));

		groupRepository.save(existsByNameExistingNameLongerNameGroup);

		assertTrue(groupRepository.existsByName("existsByName_ExistingName_LongerName"));

	}

	@Test
	public void existsByName_NonExistentName_ReturnsFalse() {

		assertFalse(groupRepository.existsByName("existsByName_NonExistentName"));

	}

	@Test
	public void count_KnownNumberOfGroups_ReturnsCorrectNumber() {

		groupRepository.deleteAll();
		assertEquals(0, groupRepository.count());

		groupRepository.save(Group.builder().build());
		assertEquals(1, groupRepository.count());

		groupRepository.save(Group.builder().build());
		assertEquals(2, groupRepository.count());

		groupRepository.save(Group.builder().build());
		assertEquals(3, groupRepository.count());

		groupRepository.save(Group.builder().build());
		assertEquals(4, groupRepository.count());

		groupRepository.save(Group.builder().build());
		assertEquals(5, groupRepository.count());

	}

	@Test
	public void save_DuplicateGroupName_ThrowsException() {

		Group duplicateGroupName1 = Group.builder().name("duplicateGroupName").build();
		Group duplicateGroupName2 = Group.builder().name("duplicateGroupName").build();

		groupRepository.save(duplicateGroupName1);

		assertThrows(DbActionExecutionException.class, () -> {
			groupRepository.save(duplicateGroupName2);
		});

	}

	@Test
	public void save_ValidGroup_ContainedInAllGroups() {

		Group validGroup1 = groupRepository.save(Group.builder().name("save_ValidGroup1").build());
		Group validGroup2 = groupRepository.save(Group.builder().name("save_ValidGroup2").build());
		Group validGroup3 = groupRepository.save(Group.builder().name("save_ValidGroup3").build());

		List<Group> allGroups = (List<Group>) groupRepository.findAll();

		assertTrue(allGroups.contains(validGroup1), "List of groups should contain added groups");
		assertTrue(allGroups.contains(validGroup2), "List of groups should contain added groups");
		assertTrue(allGroups.contains(validGroup3), "List of groups should contain added groups");

	}

	@Test
	public void deleteAll_ExistingGroups_DeletesAll() {

		assertEquals(0, groupRepository.count());

		groupRepository.save(Group.builder().name("deleteAll_DeletesAll_group1").build());

		assertEquals(1, groupRepository.count());

		groupRepository.deleteAll();

		assertEquals(0, groupRepository.count());

		groupRepository.save(Group.builder().name("deleteAll_DeletesAll_group2").build());
		groupRepository.save(Group.builder().name("deleteAll_DeletesAll_group3").build());
		groupRepository.save(Group.builder().name("deleteAll_DeletesAll_group4").build());
		groupRepository.save(Group.builder().name("deleteAll_DeletesAll_group5").build());

		assertEquals(4, groupRepository.count());

		groupRepository.deleteAll();

		assertEquals(0, groupRepository.count());

	}

	@Test
	public void deleteAll_NoGroups_DoesNothing() {

		assertEquals(0, groupRepository.count());

		groupRepository.deleteAll();

		assertEquals(0, groupRepository.count());

	}

	@Test
	public void deleteById_ValidId_DeletesGroup() {

		Group returnedGroup = groupRepository.save(Group.builder().build());

		groupRepository.deleteById(returnedGroup.getId());

		assertFalse(groupRepository.existsById(returnedGroup.getId()));

	}

	@Test
	public void deleteByName_NonExistentName_ThrowsException() {

		assertFalse(groupRepository.findByName("deleteByName_NonExistentName").isPresent());

	}

	@Test
	public void deleteByName_ValidName_DeletesGroup() {

		String nameToDelete = "delete_valid_name";

		Group delete_ValidName_Group = Group.builder().name(nameToDelete).build();

		groupRepository.save(delete_ValidName_Group);

		groupRepository.deleteByName(nameToDelete);

		assertFalse(groupRepository.findByName(nameToDelete).isPresent());

		assertFalse(groupRepository.existsByName(nameToDelete));

	}

	@Test
	public void findAll_ReturnsGroups() {

		groupRepository.deleteAll();

		assertTrue(groupRepository.count() == 0);

		Group findAll_ReturnsGroups_group1 = groupRepository
				.save(Group.builder().name("findAll_ReturnsGroups_group1").build());
		Group findAll_ReturnsGroups_group2 = groupRepository
				.save(Group.builder().name("findAll_ReturnsGroups_group2").build());
		Group findAll_ReturnsGroups_group3 = groupRepository
				.save(Group.builder().name("findAll_ReturnsGroups_group3").build());
		Group findAll_ReturnsGroups_group4 = groupRepository
				.save(Group.builder().name("findAll_ReturnsGroups_group4").build());

		assertTrue(groupRepository.existsByName("findAll_ReturnsGroups_group1"));
		assertTrue(groupRepository.existsByName("findAll_ReturnsGroups_group2"));
		assertTrue(groupRepository.existsByName("findAll_ReturnsGroups_group3"));
		assertTrue(groupRepository.existsByName("findAll_ReturnsGroups_group4"));

		List<Group> groups = (List<Group>) groupRepository.findAll();

		assertEquals(4, groups.size());

		assertTrue(groups.contains(findAll_ReturnsGroups_group1));
		assertTrue(groups.contains(findAll_ReturnsGroups_group2));
		assertTrue(groups.contains(findAll_ReturnsGroups_group3));
		assertTrue(groups.contains(findAll_ReturnsGroups_group4));

	}

	@Test
	public void findById_NonExistentId_ThrowsException() {

		assertFalse(groupRepository.findById(123456789L).isPresent());

	}

	@Test
	public void findById_ValidId_CanFindGroup() {

		Group findGroupById_validId_Group = groupRepository.save(Group.builder().build());

		Optional<Group> returnedGroup = groupRepository.findById(findGroupById_validId_Group.getId());

		assertTrue(returnedGroup.isPresent());

		assertEquals(returnedGroup.get(), findGroupById_validId_Group);

	}

	@Test
	public void findByName_NonExistentName_ReturnsNull() {

		assertFalse(groupRepository.findByName("findGroupByName_NonExistentName").isPresent());

	}

	@Test
	public void findByName_ValidName_CanFindGroup() {

		String nameToFind = "findByName_ValidName";

		Group findGroupByName_validName_Group = groupRepository
				.save(Group.builder().name("findByName_ValidName").build());

		Optional<Group> returnedGroup = groupRepository.findByName(nameToFind);

		assertTrue(returnedGroup.isPresent());

		assertEquals(returnedGroup.get(), findGroupByName_validName_Group);

		assertEquals(returnedGroup.get().getName(), findGroupByName_validName_Group.getName());

	}

}
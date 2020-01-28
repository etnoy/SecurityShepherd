package org.owasp.securityshepherd.test.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.dao.GroupDao;
import org.owasp.securityshepherd.model.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class GroupDaoTest {

	@Autowired
	private GroupDao groupDao;

	@Test
	public void containsId_ExistingId_ReturnsTrue() {

		Group containsIdExistingIdGroup = Group.builder().id("containsId_ExistingId").build();

		assertFalse(groupDao.containsId("containsId_ExistingId"));

		groupDao.create(containsIdExistingIdGroup);

		assertTrue(groupDao.containsId("containsId_ExistingId"));

		Group containsIdExistingIdLongerIdGroup = Group.builder().id("containsId_ExistingId_LongerId").build();

		assertFalse(groupDao.containsId("containsId_ExistingId_LongerId"));

		groupDao.create(containsIdExistingIdLongerIdGroup);

		assertTrue(groupDao.containsId("containsId_ExistingId_LongerId"));

	}

	@Test
	public void containsId_InvalidId_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			groupDao.containsId("");
		});

	}

	@Test
	public void containsId_NonExistentId_ReturnsFalse() {

		assertFalse(groupDao.containsId("containsId_NonExistentId"));

	}

	@Test
	public void containsName_ExistingName_ReturnsTrue() {

		Group containsNameExistingNameGroup = Group.builder().name("containsName_ExistingName").build();

		assertFalse(groupDao.containsName("containsName_ExistingName"));

		groupDao.create(containsNameExistingNameGroup);

		assertTrue(groupDao.containsName("containsName_ExistingName"));

		Group containsNameExistingNameLongerNameGroup = Group.builder().name("containsName_ExistingName_LongerName")
				.build();

		assertFalse(groupDao.containsName("containsName_ExistingName_LongerName"));

		groupDao.create(containsNameExistingNameLongerNameGroup);

		assertTrue(groupDao.containsName("containsName_ExistingName_LongerName"));

	}

	@Test
	public void containsName_InvalidName_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			groupDao.containsName("");
		});

	}

	@Test
	public void containsName_NonExistentName_ReturnsFalse() {

		assertFalse(groupDao.containsName("containsName_NonExistentName"));

	}

	@Test
	public void count_KnownNumberOfGroups_ReturnsCorrectNumber() {

		groupDao.deleteAll();
		assertEquals(0, groupDao.count());

		groupDao.create(Group.builder().build());
		assertEquals(1, groupDao.count());

		groupDao.create(Group.builder().build());
		assertEquals(2, groupDao.count());

		groupDao.create(Group.builder().build());
		assertEquals(3, groupDao.count());

		groupDao.create(Group.builder().build());
		assertEquals(4, groupDao.count());

		groupDao.create(Group.builder().build());
		assertEquals(5, groupDao.count());

	}

	@Test
	public void create_DuplicateGroupId_ThrowsException() {

		Group duplicateGroupId1 = Group.builder().id("duplicateGroupId").name("duplicateGroupId1").build();
		Group duplicateGroupId2 = Group.builder().id("duplicateGroupId").name("duplicateGroupId2").build();

		groupDao.create(duplicateGroupId1);

		assertThrows(DuplicateKeyException.class, () -> {
			groupDao.create(duplicateGroupId2);
		});

	}

	@Test
	public void create_DuplicateGroupName_ThrowsException() {

		Group duplicateGroupName1 = Group.builder().name("duplicateGroupName").build();
		Group duplicateGroupName2 = Group.builder().name("duplicateGroupName").build();

		groupDao.create(duplicateGroupName1);

		assertThrows(DuplicateKeyException.class, () -> {
			groupDao.create(duplicateGroupName2);
		});

	}

	@Test
	public void create_ValidGroup_ContainedInAllGroups() {

		Group validGroup1 = Group.builder().id("validgroup1").name("A simple groupname").build();

		Group validGroup2 = Group.builder().id("validgroup2").name("Anothergroupname").build();

		Group validGroup3 = Group.builder().id("validgroup3").name("nönlätiñchåracters").build();

		groupDao.create(validGroup1);
		groupDao.create(validGroup2);
		groupDao.create(validGroup3);

		List<Group> allGroups = groupDao.getAll();

		assertTrue(allGroups.contains(validGroup1), "List of groups should contain added groups");
		assertTrue(allGroups.contains(validGroup2), "List of groups should contain added groups");
		assertTrue(allGroups.contains(validGroup3), "List of groups should contain added groups");

	}

	@Test
	public void deleteAll_ExistingGroups_DeletesAll() {

		Group deleteAll_DeletesAll_group1 = Group.builder().id("deleteAll_DeletesAll_group1").build();
		Group deleteAll_DeletesAll_group2 = Group.builder().id("deleteAll_DeletesAll_group2").build();
		Group deleteAll_DeletesAll_group3 = Group.builder().id("deleteAll_DeletesAll_group3").build();
		Group deleteAll_DeletesAll_group4 = Group.builder().id("deleteAll_DeletesAll_group4").build();

		assertEquals(0, groupDao.count());

		groupDao.create(deleteAll_DeletesAll_group1);

		assertEquals(1, groupDao.count());

		groupDao.deleteAll();

		assertEquals(0, groupDao.count());

		groupDao.create(deleteAll_DeletesAll_group1);
		groupDao.create(deleteAll_DeletesAll_group2);
		groupDao.create(deleteAll_DeletesAll_group3);
		groupDao.create(deleteAll_DeletesAll_group4);

		assertEquals(4, groupDao.count());

		groupDao.deleteAll();

		assertEquals(0, groupDao.count());

	}

	@Test
	public void deleteAll_NoGroups_DoesNothing() {

		assertEquals(0, groupDao.count());

		groupDao.deleteAll();

		assertEquals(0, groupDao.count());

	}

	@Test
	public void deleteById_InvalidId_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			groupDao.deleteById("");
		});

	}

	@Test
	public void deleteById_NonExistentId_ThrowsException() {

		assertThrows(EmptyResultDataAccessException.class, () -> {
			groupDao.getById("deleteById_NonExistentId");
		});

	}

	@Test
	public void deleteById_ValidId_DeletesGroup() {

		String idToDelete = "delete_valid_id";

		Group delete_ValidId_Group = Group.builder().id(idToDelete).build();

		groupDao.create(delete_ValidId_Group);

		groupDao.deleteById(idToDelete);

		assertThrows(EmptyResultDataAccessException.class, () -> {
			groupDao.getById(idToDelete);
		});

		assertFalse(groupDao.containsId(idToDelete));

	}

	@Test
	public void deleteByName_InvalidName_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			groupDao.deleteByName("");
		});

	}

	@Test
	public void deleteByName_NonExistentName_ThrowsException() {

		assertThrows(EmptyResultDataAccessException.class, () -> {
			groupDao.getByName("deleteByName_NonExistentName");
		});

	}

	@Test
	public void deleteByName_ValidName_DeletesGroup() {

		String nameToDelete = "delete_valid_name";

		Group delete_ValidName_Group = Group.builder().name(nameToDelete).build();

		groupDao.create(delete_ValidName_Group);

		groupDao.deleteByName(nameToDelete);

		assertThrows(EmptyResultDataAccessException.class, () -> {
			groupDao.getByName(nameToDelete);
		});

		assertFalse(groupDao.containsName(nameToDelete));

	}

	@Test
	public void getAll_ReturnsGroups() {

		groupDao.deleteAll();

		assertTrue(groupDao.count() == 0);

		Group getAll_ReturnsGroups_group1 = Group.builder().id("getAll_ReturnsGroups_group1").build();
		Group getAll_ReturnsGroups_group2 = Group.builder().id("getAll_ReturnsGroups_group2").build();
		Group getAll_ReturnsGroups_group3 = Group.builder().id("getAll_ReturnsGroups_group3").build();
		Group getAll_ReturnsGroups_group4 = Group.builder().id("getAll_ReturnsGroups_group4").build();

		groupDao.create(getAll_ReturnsGroups_group1);
		groupDao.create(getAll_ReturnsGroups_group2);
		groupDao.create(getAll_ReturnsGroups_group3);
		groupDao.create(getAll_ReturnsGroups_group4);

		assertTrue(groupDao.containsId(getAll_ReturnsGroups_group1.getId()));
		assertTrue(groupDao.containsId(getAll_ReturnsGroups_group2.getId()));
		assertTrue(groupDao.containsId(getAll_ReturnsGroups_group3.getId()));
		assertTrue(groupDao.containsId(getAll_ReturnsGroups_group4.getId()));

		List<Group> groups = groupDao.getAll();

		assertEquals(4, groups.size());

		assertTrue(groups.contains(getAll_ReturnsGroups_group1));
		assertTrue(groups.contains(getAll_ReturnsGroups_group2));
		assertTrue(groups.contains(getAll_ReturnsGroups_group3));
		assertTrue(groups.contains(getAll_ReturnsGroups_group4));

	}

	@Test
	public void getById_InvalidId_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			groupDao.getById("");
		});

	}

	@Test
	public void getById_NonExistentId_ThrowsException() {

		assertThrows(EmptyResultDataAccessException.class, () -> {
			groupDao.getById("getGroupById_NonExistentId");
		});

	}

	@Test
	public void getById_ValidId_CanFindGroup() {

		String idToFind = "getGroupByIdvalidId";

		Group getGroupById_validId_Group = Group.builder().id(idToFind).build();

		groupDao.create(getGroupById_validId_Group);

		Group returnedGroup = groupDao.getById(idToFind);

		assertEquals(returnedGroup, getGroupById_validId_Group);

	}

	@Test
	public void getByName_InvalidName_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			groupDao.getByName("");
		});

	}

	@Test
	public void getByName_NonExistentName_ThrowsException() {

		assertThrows(EmptyResultDataAccessException.class, () -> {
			groupDao.getByName("getGroupByName_NonExistentName");
		});

	}

	@Test
	public void getByName_ValidName_CanFindGroup() {

		String nameToFind = "getGroupByNamevalidName";

		Group getGroupByName_validName_Group = Group.builder().name(nameToFind).build();

		groupDao.create(getGroupByName_validName_Group);

		Group returnedGroup = groupDao.getByName(nameToFind);

		assertEquals(returnedGroup, getGroupByName_validName_Group);

		assertEquals(returnedGroup.getName(), getGroupByName_validName_Group.getName());

	}

	@Test
	public void renameById_DuplicateName_ThrowsException() {

		String idToRename = "renameById_DupName_renameId";
		String idOfDuplicate = "renameById_DupName_duplicateId";

		String oldName = "renameById_DuplicateName_oldName";
		String duplicateName = "renameById_DuplicateName_duplicateName";

		Group renameGroup = Group.builder().id(idToRename).name(oldName).build();
		Group duplicateGroup = Group.builder().id(idOfDuplicate).name(duplicateName).build();

		groupDao.create(renameGroup);
		groupDao.create(duplicateGroup);

		int countBefore = groupDao.count();

		assertThrows(DuplicateKeyException.class, () -> {
			groupDao.renameById(idToRename, duplicateName);
		});

		int countAfter = groupDao.count();

		assertEquals(countBefore, countAfter);

	}

	@Test
	public void renameById_InvalidId_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			groupDao.renameById("", "renameById_InvalidId");
		});

	}

	@Test
	public void renameById_InvalidName_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			groupDao.renameById("renameById_InvalidName", "");
		});

	}

	@Test
	public void renameById_NonExistentId_ThrowsException() {

		assertThrows(JdbcUpdateAffectedIncorrectNumberOfRowsException.class, () -> {
			groupDao.renameById("renameById_NonExistentId_id", "renameById_NonExistentId_groupname");
		});

	}

	@Test
	public void renameById_ValidIdAndName_ChangesName() {

		String idToRename = "changeNameById_ValidIdAndName";

		String oldName = "changeNameById_ValidIdAndName_oldName";
		String newName = "changeNameById_ValidIdAndName_newName";

		Group insertedGroup = Group.builder().id(idToRename).name(oldName).build();

		groupDao.create(insertedGroup);

		groupDao.renameById(idToRename, newName);

		Group returnedGroup = groupDao.getById(idToRename);

		assertEquals(returnedGroup, insertedGroup);

		assertEquals(returnedGroup.getName(), newName);

	}

	@Test
	public void renameByName_DuplicateName_ThrowsException() {

		String oldName = "changeNameById_DuplicateName_oldName";
		String duplicateName = "changeNameById_DuplicateName_duplicateName";

		Group renameGroup = Group.builder().name(oldName).build();
		Group duplicateGroup = Group.builder().name(duplicateName).build();

		groupDao.create(renameGroup);
		groupDao.create(duplicateGroup);

		int countBefore = groupDao.count();

		assertThrows(DuplicateKeyException.class, () -> {
			groupDao.renameByName(oldName, duplicateName);
		});

		int countAfter = groupDao.count();

		assertEquals(countBefore, countAfter);

	}

	@Test
	public void renameByName_InvalidOldName_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			groupDao.renameByName("", "renameById_InvalidId");
		});

	}

	@Test
	public void renameByName_InvalidNewName_ThrowsIllegalArgumentException() {

		Group renameGroup = Group.builder().name("renameByName_InvalidNewName").build();

		groupDao.create(renameGroup);

		assertThrows(IllegalArgumentException.class, () -> {
			groupDao.renameByName("renameByName_InvalidNewName", "");
		});

	}

	@Test
	public void renameByName_NonExistentName_ThrowsException() {

		assertThrows(JdbcUpdateAffectedIncorrectNumberOfRowsException.class, () -> {
			groupDao.renameByName("renameByName_NonExistentName_name", "renameByName_NonExistentName_groupname");
		});

	}

	@Test
	public void renameByName_ValidNames_ChangesName() {

		String oldName = "changeNameById_ValidIdAndName_oldName";
		String newName = "changeNameById_ValidIdAndName_newName";

		Group insertedGroup = Group.builder().name(oldName).build();

		groupDao.create(insertedGroup);

		groupDao.renameByName(oldName, newName);

		Group returnedGroup = groupDao.getByName(newName);

		assertEquals(returnedGroup, insertedGroup);

		assertEquals(returnedGroup.getName(), newName);

	}

}
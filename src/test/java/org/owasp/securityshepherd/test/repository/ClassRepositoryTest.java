package org.owasp.securityshepherd.test.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.ClassEntity;
import org.owasp.securityshepherd.repository.ClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class ClassRepositoryTest {

	@Autowired
	private ClassRepository classRepository;

	@Test
	public void existsById_ExistingId_ReturnsTrue() {

		ClassEntity existsByIdExistingIdClass = ClassEntity.builder().name("TestClass").build();

		ClassEntity returnedClass = classRepository.save(existsByIdExistingIdClass);

		assertNotNull(returnedClass.getId());
		assertTrue(classRepository.existsById(returnedClass.getId()));

	}

	@Test
	public void existsById_NonExistentId_ReturnsFalse() {

		assertFalse(classRepository.existsById(1234567890));

	}

	@Test
	public void existsByName_ExistingName_ReturnsTrue() {

		ClassEntity existsByNameExistingNameClass = ClassEntity.builder().name("existsByName_ExistingName").build();

		assertFalse(classRepository.existsByName("existsByName_ExistingName"));

		classRepository.save(existsByNameExistingNameClass);

		assertTrue(classRepository.existsByName("existsByName_ExistingName"));

		ClassEntity existsByNameExistingNameLongerNameClass = ClassEntity.builder().name("existsByName_ExistingName_LongerName")
				.build();

		assertFalse(classRepository.existsByName("existsByName_ExistingName_LongerName"));

		classRepository.save(existsByNameExistingNameLongerNameClass);

		assertTrue(classRepository.existsByName("existsByName_ExistingName_LongerName"));

	}

	@Test
	public void existsByName_NonExistentName_ReturnsFalse() {

		assertFalse(classRepository.existsByName("existsByName_NonExistentName"));

	}

	@Test
	public void count_KnownNumberOfClasss_ReturnsCorrectNumber() {

		classRepository.deleteAll();
		assertEquals(0, classRepository.count());

		classRepository.save(ClassEntity.builder().name("testClass1").build());
		assertEquals(1, classRepository.count());

		classRepository.save(ClassEntity.builder().name("testClass2").build());
		assertEquals(2, classRepository.count());

		classRepository.save(ClassEntity.builder().name("testClass3").build());
		assertEquals(3, classRepository.count());

		classRepository.save(ClassEntity.builder().name("testClass4").build());
		assertEquals(4, classRepository.count());

		classRepository.save(ClassEntity.builder().name("testClass5").build());
		assertEquals(5, classRepository.count());

	}

	@Test
	public void save_DuplicateClassName_ThrowsException() {

		ClassEntity duplicateClassName1 = ClassEntity.builder().name("duplicateClassName").build();
		ClassEntity duplicateClassName2 = ClassEntity.builder().name("duplicateClassName").build();

		classRepository.save(duplicateClassName1);

		assertThrows(DbActionExecutionException.class, () -> {
			classRepository.save(duplicateClassName2);
		});

	}

	@Test
	public void save_ValidClass_ContainedInAllClasss() {

		ClassEntity validClass1 = classRepository.save(ClassEntity.builder().name("save_ValidClass1").build());
		ClassEntity validClass2 = classRepository.save(ClassEntity.builder().name("save_ValidClass2").build());
		ClassEntity validClass3 = classRepository.save(ClassEntity.builder().name("save_ValidClass3").build());

		List<ClassEntity> allClasss = (List<ClassEntity>) classRepository.findAll();

		assertTrue(allClasss.contains(validClass1), "List of classs should contain added classs");
		assertTrue(allClasss.contains(validClass2), "List of classs should contain added classs");
		assertTrue(allClasss.contains(validClass3), "List of classs should contain added classs");

	}

	@Test
	public void deleteAll_ExistingClasss_DeletesAll() {

		assertEquals(0, classRepository.count());

		classRepository.save(ClassEntity.builder().name("deleteAll_DeletesAll_class1").build());

		assertEquals(1, classRepository.count());

		classRepository.deleteAll();

		assertEquals(0, classRepository.count());

		classRepository.save(ClassEntity.builder().name("deleteAll_DeletesAll_class2").build());
		classRepository.save(ClassEntity.builder().name("deleteAll_DeletesAll_class3").build());
		classRepository.save(ClassEntity.builder().name("deleteAll_DeletesAll_class4").build());
		classRepository.save(ClassEntity.builder().name("deleteAll_DeletesAll_class5").build());

		assertEquals(4, classRepository.count());

		classRepository.deleteAll();

		assertEquals(0, classRepository.count());

	}

	@Test
	public void deleteAll_NoClasss_DoesNothing() {

		assertEquals(0, classRepository.count());

		classRepository.deleteAll();

		assertEquals(0, classRepository.count());

	}

	@Test
	public void deleteById_ValidId_DeletesClass() {

		ClassEntity returnedClass = classRepository.save(ClassEntity.builder().name("TestClass").build());

		classRepository.deleteById(returnedClass.getId());

		assertFalse(classRepository.existsById(returnedClass.getId()));

	}

	@Test
	public void deleteByName_NonExistentName_ThrowsException() {

		assertFalse(classRepository.findByName("deleteByName_NonExistentName").isPresent());

	}

	@Test
	public void deleteByName_ValidName_DeletesClass() {

		String nameToDelete = "delete_valid_name";

		ClassEntity delete_ValidName_Class = ClassEntity.builder().name(nameToDelete).build();

		classRepository.save(delete_ValidName_Class);

		classRepository.deleteByName(nameToDelete);

		assertFalse(classRepository.findByName(nameToDelete).isPresent());

		assertFalse(classRepository.existsByName(nameToDelete));

	}

	@Test
	public void findAll_ReturnsClasss() {

		classRepository.deleteAll();

		assertTrue(classRepository.count() == 0);

		ClassEntity findAll_ReturnsClasss_class1 = classRepository
				.save(ClassEntity.builder().name("findAll_ReturnsClasss_class1").build());
		ClassEntity findAll_ReturnsClasss_class2 = classRepository
				.save(ClassEntity.builder().name("findAll_ReturnsClasss_class2").build());
		ClassEntity findAll_ReturnsClasss_class3 = classRepository
				.save(ClassEntity.builder().name("findAll_ReturnsClasss_class3").build());
		ClassEntity findAll_ReturnsClasss_class4 = classRepository
				.save(ClassEntity.builder().name("findAll_ReturnsClasss_class4").build());

		assertTrue(classRepository.existsByName("findAll_ReturnsClasss_class1"));
		assertTrue(classRepository.existsByName("findAll_ReturnsClasss_class2"));
		assertTrue(classRepository.existsByName("findAll_ReturnsClasss_class3"));
		assertTrue(classRepository.existsByName("findAll_ReturnsClasss_class4"));

		List<ClassEntity> classs = (List<ClassEntity>) classRepository.findAll();

		assertEquals(4, classs.size());

		assertTrue(classs.contains(findAll_ReturnsClasss_class1));
		assertTrue(classs.contains(findAll_ReturnsClasss_class2));
		assertTrue(classs.contains(findAll_ReturnsClasss_class3));
		assertTrue(classs.contains(findAll_ReturnsClasss_class4));

	}

	@Test
	public void findById_NonExistentId_ThrowsException() {

		assertFalse(classRepository.findById(123456789).isPresent());

	}

	@Test
	public void findById_ValidId_CanFindClass() {

		ClassEntity findClassById_validId_Class = classRepository.save(ClassEntity.builder().name("TestClass").build());

		Optional<ClassEntity> returnedClass = classRepository.findById(findClassById_validId_Class.getId());

		assertTrue(returnedClass.isPresent());

		assertEquals(returnedClass.get(), findClassById_validId_Class);

	}

	@Test
	public void findByName_NonExistentName_ReturnsNull() {

		assertFalse(classRepository.findByName("findClassByName_NonExistentName").isPresent());

	}

	@Test
	public void findByName_ValidName_CanFindClass() {

		String nameToFind = "findByName_ValidName";

		ClassEntity findClassByName_validName_Class = classRepository
				.save(ClassEntity.builder().name("findByName_ValidName").build());

		Optional<ClassEntity> returnedClass = classRepository.findByName(nameToFind);

		assertTrue(returnedClass.isPresent());

		assertEquals(returnedClass.get(), findClassByName_validName_Class);

		assertEquals(returnedClass.get().getName(), findClassByName_validName_Class.getName());

	}

}
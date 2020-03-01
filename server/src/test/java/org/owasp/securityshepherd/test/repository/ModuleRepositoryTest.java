package org.owasp.securityshepherd.test.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.persistence.model.Module;
import org.owasp.securityshepherd.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ModuleRepositoryTest {

	@Autowired
	private ModuleRepository moduleRepository;

	@Test
	public void existsById_ExistingId_ReturnsTrue() {

		Module existsByIdExistingIdModule = Module.builder().name("existsById_ExistingId").build();

		Module returnedModule = moduleRepository.save(existsByIdExistingIdModule).block();

		assertNotNull(returnedModule.getId());
		assertTrue(moduleRepository.existsById(returnedModule.getId()).block());

	}

	@Test
	public void existsById_NonExistentId_ReturnsFalse() {

		assertFalse(moduleRepository.existsById(1234567890).block());

	}

	@Test
	public void existsByName_ExistingName_ReturnsTrue() {

		Module existsByNameExistingNameModule = Module.builder().name("existsByName_ExistingName").build();

		assertFalse(moduleRepository.existsByName("existsByName_ExistingName").block());

		moduleRepository.save(existsByNameExistingNameModule);

		assertTrue(moduleRepository.existsByName("existsByName_ExistingName").block());

		Module existsByNameExistingNameLongerNameModule = Module.builder().name("existsByName_ExistingName_LongerName")
				.build();

		assertFalse(moduleRepository.existsByName("existsByName_ExistingName_LongerName").block());

		moduleRepository.save(existsByNameExistingNameLongerNameModule);

		assertTrue(moduleRepository.existsByName("existsByName_ExistingName_LongerName").block());

	}

	@Test
	public void existsByName_NonExistentName_ReturnsFalse() {

		assertFalse(moduleRepository.existsByName("existsByName_NonExistentName").block());

	}

	@Test
	public void count_KnownNumberOfModules_ReturnsCorrectNumber() {

		moduleRepository.deleteAll();
		assertEquals(0, moduleRepository.count());

		moduleRepository.save(Module.builder().name("count_KnownNumberOfModules1").build());
		assertEquals(1, moduleRepository.count());

		moduleRepository.save(Module.builder().name("count_KnownNumberOfModules2").build());
		assertEquals(2, moduleRepository.count());

		moduleRepository.save(Module.builder().name("count_KnownNumberOfModules3").build());
		assertEquals(3, moduleRepository.count());

		moduleRepository.save(Module.builder().name("count_KnownNumberOfModules4").build());
		assertEquals(4, moduleRepository.count());

		moduleRepository.save(Module.builder().name("count_KnownNumberOfModules5").build());
		assertEquals(5, moduleRepository.count());

	}

	@Test
	public void save_DuplicateModuleName_ThrowsException() {

		Module duplicateModuleName1 = Module.builder().name("duplicateModuleName").build();
		Module duplicateModuleName2 = Module.builder().name("duplicateModuleName").build();

		moduleRepository.save(duplicateModuleName1);

		assertThrows(DbActionExecutionException.class, () -> {
			moduleRepository.save(duplicateModuleName2);
		});

	}

	@Test
	public void save_ValidModule_ContainedInAllModules() {

		Module validModule1 = moduleRepository.save(Module.builder().name("save_ValidModule1").build()).block();
		Module validModule2 = moduleRepository.save(Module.builder().name("save_ValidModule2").build()).block();
		Module validModule3 = moduleRepository.save(Module.builder().name("save_ValidModule3").build()).block();

		List<Module> allModules = (List<Module>) moduleRepository.findAll().collectList().block();

		assertTrue(allModules.contains(validModule1), "List of modules should contain added modules");
		assertTrue(allModules.contains(validModule2), "List of modules should contain added modules");
		assertTrue(allModules.contains(validModule3), "List of modules should contain added modules");

	}

	@Test
	public void deleteAll_ExistingModules_DeletesAll() {

		assertEquals(0, moduleRepository.count());

		moduleRepository.save(Module.builder().name("deleteAll_DeletesAll_module1").build());

		assertEquals(1, moduleRepository.count());

		moduleRepository.deleteAll();

		assertEquals(0, moduleRepository.count());

		moduleRepository.save(Module.builder().name("deleteAll_DeletesAll_module2").build());
		moduleRepository.save(Module.builder().name("deleteAll_DeletesAll_module3").build());
		moduleRepository.save(Module.builder().name("deleteAll_DeletesAll_module4").build());
		moduleRepository.save(Module.builder().name("deleteAll_DeletesAll_module5").build());

		assertEquals(4, moduleRepository.count());

		moduleRepository.deleteAll();

		assertEquals(0, moduleRepository.count());

	}

	@Test
	public void deleteAll_NoModules_DoesNothing() {

		assertEquals(0, moduleRepository.count());

		moduleRepository.deleteAll();

		assertEquals(0, moduleRepository.count());

	}

	@Test
	public void deleteById_ValidId_DeletesModule() {

		Module returnedModule = moduleRepository.save(Module.builder().name("deleteById_ValidId").build()).block();

		moduleRepository.deleteById(returnedModule.getId());

		assertFalse(moduleRepository.existsById(returnedModule.getId()).block());

	}

	@Test
	public void deleteByName_NonExistentName_ThrowsException() {

		assertNull(moduleRepository.findByName("deleteByName_NonExistentName").block());

	}

	@Test
	public void deleteByName_ValidName_DeletesModule() {

		String nameToDelete = "delete_valid_name";

		Module delete_ValidName_Module = Module.builder().name(nameToDelete).build();

		moduleRepository.save(delete_ValidName_Module);

		moduleRepository.deleteByName(nameToDelete);

		assertNull(moduleRepository.findByName(nameToDelete).block());

		assertNull(moduleRepository.existsByName(nameToDelete).block());

	}

	@Test
	public void findAll_ReturnsModules() {

		moduleRepository.deleteAll();

		assertTrue(moduleRepository.count().block() == 0);

		Module findAll_ReturnsModules_module1 = moduleRepository
				.save(Module.builder().name("findAll_ReturnsModules_module1").build()).block();
		Module findAll_ReturnsModules_module2 = moduleRepository
				.save(Module.builder().name("findAll_ReturnsModules_module2").build()).block();
		Module findAll_ReturnsModules_module3 = moduleRepository
				.save(Module.builder().name("findAll_ReturnsModules_module3").build()).block();
		Module findAll_ReturnsModules_module4 = moduleRepository
				.save(Module.builder().name("findAll_ReturnsModules_module4").build()).block();

		assertTrue(moduleRepository.existsByName("findAll_ReturnsModules_module1").block());
		assertTrue(moduleRepository.existsByName("findAll_ReturnsModules_module2").block());
		assertTrue(moduleRepository.existsByName("findAll_ReturnsModules_module3").block());
		assertTrue(moduleRepository.existsByName("findAll_ReturnsModules_module4").block());

		List<Module> modules = (List<Module>) moduleRepository.findAll().collectList().block();

		assertEquals(4, modules.size());

		assertTrue(modules.contains(findAll_ReturnsModules_module1));
		assertTrue(modules.contains(findAll_ReturnsModules_module2));
		assertTrue(modules.contains(findAll_ReturnsModules_module3));
		assertTrue(modules.contains(findAll_ReturnsModules_module4));

	}

	@Test
	public void findById_NonExistentId_ThrowsException() {

		assertNull(moduleRepository.findById(123456789));

	}

	@Test
	public void findById_ValidId_CanFindModule() {

		Module findModuleById_validId_Module = moduleRepository.save(Module.builder().name("findById_ValidId").build()).block();

		Module returnedModule = moduleRepository.findById(findModuleById_validId_Module.getId()).block();

		assertNotNull(returnedModule);

		assertEquals(returnedModule, findModuleById_validId_Module);

	}

	@Test
	public void findByName_NonExistentName_ReturnsNull() {

		assertNull(moduleRepository.findByName("findModuleByName_NonExistentName").block());

	}

	@Test
	public void findByName_ValidName_CanFindModule() {

		String nameToFind = "findByName_ValidName";

		Module findModuleByName_validName_Module = moduleRepository
				.save(Module.builder().name("findByName_ValidName").build()).block();

		Module returnedModule = moduleRepository.findByName(nameToFind).block();

		assertNotNull(returnedModule);

		assertEquals(returnedModule, findModuleByName_validName_Module);

		assertEquals(returnedModule.getName(), findModuleByName_validName_Module.getName());

	}

}
package org.owasp.securityshepherd.repository;

import java.util.Optional;

import org.owasp.securityshepherd.model.User;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

	@Query("select count(1) from users where name = :name")
	public boolean existsByName(String name);

	@Modifying
	@Query("delete from users where name = :name")
	public void deleteByName(String name);

	@Query("SELECT users.id AS id, users.name AS name, users.email AS email, users.class_id AS class_id, auth_data.user AS auth_data_user, users.solution_key AS solution_key, auth_data_saml.user AS auth_data_saml_user, auth_data_password.user AS auth_data_password_user, auth_data.is_enabled AS auth_data_is_enabled, auth_data.last_login AS auth_data_last_login, auth_data.bad_login_count AS auth_data_bad_login_count, auth_data.is_admin AS auth_data_is_admin, auth_data.suspended_until AS auth_data_suspended_until, auth_data.suspension_message AS auth_data_suspension_message, auth_data_saml.saml_id AS auth_data_saml_saml_id, auth_data_password.hashed_password AS auth_data_password_hashed_password, auth_data_password.password_expired AS auth_data_password_password_expired FROM users LEFT OUTER JOIN auth_data AS auth_data ON auth_data.user = users.id LEFT OUTER JOIN auth_data_saml AS auth_data_saml ON auth_data_saml.user = users.id LEFT OUTER JOIN auth_data_password AS auth_data_password ON auth_data_password.user = users.id WHERE users.name = :name")
	public Optional<User> findByName(String name);

}
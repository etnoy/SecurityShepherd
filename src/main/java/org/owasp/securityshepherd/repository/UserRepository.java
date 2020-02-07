package org.owasp.securityshepherd.repository;

import java.util.Optional;

import org.owasp.securityshepherd.model.User;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

	@Query("select count(1) from users where name = :name")
	public boolean existsByName(@Param("name") String name);

	@Modifying
	@Query("delete from users where name = :name")
	public void deleteByName(@Param("name") String name);

	@Query("SELECT users.id AS id, users.name AS name, auth.user AS auth_user, users.email AS email, users.class_id AS class_id, auth_saml.user AS auth_saml_user, auth.is_admin AS auth_is_admin, auth_password.user AS auth_password_user, auth.last_login AS auth_last_login, auth.is_enabled AS auth_is_enabled, auth.bad_login_count AS auth_bad_login_count, auth.suspended_until AS auth_suspended_until, auth.last_login_method AS auth_last_login_method, auth.suspension_message AS auth_suspension_message, auth_saml.saml_id AS auth_saml_saml_id, auth_password.login_name AS auth_password_login_name, auth_password.hashed_password AS auth_password_hashed_password, auth_password.password_expired AS auth_password_password_expired FROM users LEFT OUTER JOIN auth AS auth ON auth.user = users.id LEFT OUTER JOIN saml_auth AS auth_saml ON auth_saml.user = users.id LEFT OUTER JOIN password_auth AS auth_password ON auth_password.user = users.id WHERE users.name = :name")
	public Optional<User> findByName(@Param("name") String name);
	
	@Query("SELECT users.id AS id, users.name AS name, auth.user AS auth_user, users.email AS email, users.class_id AS class_id, auth_saml.user AS auth_saml_user, auth.is_admin AS auth_is_admin, auth_password.user AS auth_password_user, auth.last_login AS auth_last_login, auth.is_enabled AS auth_is_enabled, auth.bad_login_count AS auth_bad_login_count, auth.suspended_until AS auth_suspended_until, auth.last_login_method AS auth_last_login_method, auth.suspension_message AS auth_suspension_message, auth_saml.saml_id AS auth_saml_saml_id, auth_password.login_name AS auth_password_login_name, auth_password.hashed_password AS auth_password_hashed_password, auth_password.password_expired AS auth_password_password_expired FROM users LEFT OUTER JOIN auth AS auth ON auth.user = users.id LEFT OUTER JOIN saml_auth AS auth_saml ON auth_saml.user = users.id LEFT OUTER JOIN password_auth AS auth_password ON auth_password.user = users.id WHERE auth_password.login_name = :login_name")
	public Optional<User> findByLoginName(@Param("login_name") String loginName);

}
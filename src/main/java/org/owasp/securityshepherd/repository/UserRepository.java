package org.owasp.securityshepherd.repository;

import java.util.Optional;

import org.owasp.securityshepherd.model.User;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
	@Query("SELECT user.id AS id, user.display_name AS display_name, auth.user AS auth_user, user.email AS email, user.class_id AS class_id, auth_saml.user AS auth_saml_user, auth.is_admin AS auth_is_admin, auth_password.user AS auth_password_user, auth.account_created AS auth_account_created, auth.last_login AS auth_last_login, auth.is_enabled AS auth_is_enabled, auth.bad_login_count AS auth_bad_login_count, auth.suspended_until AS auth_suspended_until, auth.last_login_method AS auth_last_login_method, auth.suspension_message AS auth_suspension_message, auth_saml.saml_id AS auth_saml_saml_id, auth_password.login_name AS auth_password_login_name, auth_password.hashed_password AS auth_password_hashed_password, auth_password.password_expired AS auth_password_password_expired FROM user LEFT OUTER JOIN auth AS auth ON auth.user = user.id LEFT OUTER JOIN saml_auth AS auth_saml ON auth_saml.user = user.id LEFT OUTER JOIN password_auth AS auth_password ON auth_password.user = user.id WHERE auth_password.login_name = :login_name")
	public Optional<User> findByLoginName(@Param("login_name") String loginName);
}
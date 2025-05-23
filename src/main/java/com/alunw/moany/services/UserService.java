package com.alunw.moany.services;

import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.alunw.moany.model.User;
import com.alunw.moany.model.UserAuthority;
import com.alunw.moany.repository.UserAuthorityRepository;
import com.alunw.moany.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private UserAuthorityRepository authorityRepo;

	@Override
	public UserDetails loadUserByUsername(String username) {

		logger.debug("Find user {}", username);

		User user = userRepo.findByUsername(username);
		if (user == null) {
			logger.warn("Username '{}' not found", username);
			throw new UsernameNotFoundException(username);
		}

		Set<UserAuthority> authorities = authorityRepo.findByUser(user);

		logger.debug("user: username = {}, password = {}, enabled = {}, authorities = {}", user.getUsername(), user.getPassword(), user.isEnabled(), authorities);

		return new Principal(user, authorities);
	}

	public static class Principal implements UserDetails {

		private static final long serialVersionUID = -5402266165574363310L;

		private User user;
		private Set<UserAuthority> authorities;

		public Principal(User user, Set<UserAuthority> authorities) {
			this.user = user;
			this.authorities = authorities;
		}

		public User getUser() {
			return user;
		}

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return authorities;
		}

		@Override
		public String getPassword() {
			return user.getPassword();
		}

		@Override
		public String getUsername() {
			return user.getUsername();
		}

		@Override
		public boolean isAccountNonExpired() {
			// TODO
			return true;
		}

		@Override
		public boolean isAccountNonLocked() {
			// TODO
			return true;
		}

		@Override
		public boolean isCredentialsNonExpired() {
			// TODO
			return true;
		}

		@Override
		public boolean isEnabled() {
			return user.isEnabled();
		}
	}

}

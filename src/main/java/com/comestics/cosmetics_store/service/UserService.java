package com.comestics.cosmetics_store.service;

import com.comestics.cosmetics_store.entity.User;

public interface UserService {
    User registerCustomer(User user, String rawPassword);
    User getCurrentUser();
}

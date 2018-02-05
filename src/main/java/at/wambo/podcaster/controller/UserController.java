package at.wambo.podcaster.controller;

import at.wambo.podcaster.model.HistoryEntry;
import at.wambo.podcaster.model.User;
import at.wambo.podcaster.service.HistoryService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Martin
 *         13.08.2016
 */

@RestController
@RequestMapping(path = "/api/users")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {
    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final @NonNull HistoryService historyEntryRepository;

    private User getUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @RequestMapping(method = RequestMethod.GET)
    public User getUserInfo() {
        return getUser();
    }

    @RequestMapping(path = "/history")
    public Page<HistoryEntry> getHistoryForUser(Pageable page) {
        return historyEntryRepository.getHistoryForUser(getUser(), page);
    }

    @RequestMapping(path = "/history/grouped")
    public Page<HistoryService.GroupedHistoryEntries> groupedHistory(Pageable page) {
        return historyEntryRepository.getGroupedHistory(getUser(), page);
    }
}

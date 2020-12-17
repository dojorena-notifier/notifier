package com.epam.dojo.notifier.web;

import com.epam.dojo.notifier.api.ContestController;
import com.epam.dojo.notifier.model.Contest;
import com.epam.dojo.notifier.model.Game;
import com.epam.dojo.notifier.service.GamesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WebUIController {

    @Autowired
    private GamesService gamesService;

    @Autowired
    private ContestController contestController;

    @GetMapping("/")
    public String redirect(Model model) {
        return contestsPage(model);
    }

    @GetMapping("/contest")
    public String contestsPage(Model model) {
        return setupContestsPage(model, new Contest());
    }

    @PostMapping("/contest")
    public String newContest(@ModelAttribute Contest newContest, Model model) {
        Game selectedGame = gamesService.getGameById(newContest.getContestId());
        newContest.setTitle(selectedGame.getTitle());
        contestController.subscribeForContest(newContest);
        return setupContestsPage(model, new Contest());
    }

    @GetMapping("/contest/open/{id}")
    public String editContest(@PathVariable String id, Model model) {
        Contest existingContest = gamesService.getContestById(id);
        return setupContestsPage(model, existingContest);
    }

    @GetMapping("/contest/stop/{id}")
    public String stopContest(@PathVariable String id, Model model) {
        contestController.stopNotifications(id);
        return setupContestsPage(model, new Contest());
    }

    private String setupContestsPage(Model model, Contest contest) {
        model.addAttribute("newContest", contest);
        model.addAttribute("games", gamesService.getAllGames());
        model.addAttribute("contests", gamesService.getAllContests());
        return "contest";
    }
}

package com.votingsystem.controller;

import com.votingsystem.entity.Election;
import com.votingsystem.service.BlockchainService;
import com.votingsystem.service.CandidateService;
import com.votingsystem.service.ElectionService;
import com.votingsystem.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.Optional;

@Controller
public class PageController {

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private ElectionService electionService;

    @Autowired
    private VoteService voteService;

    @Autowired
    private BlockchainService blockchainService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("contractAddress", blockchainService.getContractAddress());
        return "index";
    }

    @GetMapping("/connect")
    public String connectWallet(Model model) {
        model.addAttribute("contractAddress", blockchainService.getContractAddress());
        return "connect-wallet";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("contractAddress", blockchainService.getContractAddress());
        return "register";
    }

    @GetMapping("/candidates")
    public String candidates(Model model) {
        Optional<Election> activeElection = electionService.getActiveElection();
        if (activeElection.isPresent()) {
            model.addAttribute("candidates", candidateService.getCandidatesByElection(activeElection.get().getId()));
            model.addAttribute("electionName", activeElection.get().getName());
        } else {
            model.addAttribute("candidates", Collections.emptyList());
            model.addAttribute("electionName", "No Active Election");
        }
        model.addAttribute("contractAddress", blockchainService.getContractAddress());
        return "candidates";
    }

    @GetMapping("/vote")
    public String vote(Model model) {
        Optional<Election> activeElection = electionService.getActiveElection();
        if (activeElection.isPresent()) {
            model.addAttribute("candidates", candidateService.getCandidatesByElection(activeElection.get().getId()));
            model.addAttribute("electionStatus", activeElection.get().getStatus());
            model.addAttribute("electionName", activeElection.get().getName());
        } else {
            model.addAttribute("candidates", Collections.emptyList());
            model.addAttribute("electionStatus", "NOT_STARTED");
            model.addAttribute("electionName", "No Active Election");
        }
        model.addAttribute("contractAddress", blockchainService.getContractAddress());
        return "vote";
    }

    @GetMapping("/results")
    public String results(Model model) {
        model.addAttribute("results", voteService.getResults());
        model.addAttribute("totalVotes", voteService.getTotalVotes());
        model.addAttribute("contractAddress", blockchainService.getContractAddress());
        return "results";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("elections", electionService.getAllElections());
        model.addAttribute("candidates", candidateService.getAllCandidates());
        model.addAttribute("results", voteService.getResults());
        model.addAttribute("totalVotes", voteService.getTotalVotes());
        model.addAttribute("contractAddress", blockchainService.getContractAddress());
        return "admin";
    }
}

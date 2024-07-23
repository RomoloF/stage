package com.example.demo.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller per gestire le richieste delle pagine.
 */
@Controller
@RequestMapping("/index")
public class Index {

    /**
     * Metodo per gestire le richieste GET alla pagina "/hello".
     * @param name Nome da visualizzare nella pagina.
     * @return La vista Thymeleaf.
     */
    @GetMapping
    public String index() {

        return "index";
    }
}

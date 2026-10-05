Controller : 
 - Indentification du vue suivant 
 - Recuperer de donner et l'envoyer dans ce vue (a voir )

Instruction : 
 - Mettre un donner en dure 
 - Dedeteminer quel est le vue suivant a aller et comment passer les donner a ce vue 
 
 -> Il faut mettre dans une fonction dans le projet teste , exemple : 

    @GetMapping("/ancienne-page")
    public function andrana(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setPage("ensuite.jsp");
        modelAndView.addObject("data", "valeur en dure");
        return modelAndView;
    }



Etape (To do)
 - Creer un model ModelAndView qui aura comme parmetre le nom du vue et les donner a passer
 - Dans FrontControllerServlet.java , specifiquement dans init , il faut 

sprint 6
 refa manao requette tsy manao requette dispacher fa json

sprint 7
- creer un formulaire dans front
- avoir une fonction dans controller
- la fonction recoit les parametres du formulaire

sprint 7 Bis
 la meme chose que 7 mais recois un objet cette fois ci 
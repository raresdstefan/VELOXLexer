VELOX Lexer — Analyseur lexical pour le langage magique VELOX

Equipe: Victor Ioan Stoian & Rares Stefan Delamarian

Description: VELOX est un langage magique ou chaque sort opere sur des vitesses (speed values). Les sorts lancent, amplifient, inversent ou verrouillent des vitesses magiques.

Parametres:
 A = 4 → Langage magique
 B = 5 → Vitesses (6+19=25; 25 mod 7 + 1 = 5)
 C = 1 → Une instruction doit commencer par un mot-cle



Token types:
 KEYWORD — CAST SURGE INVERT LOCK BOOST NULLIFY PHASE
 SPEED — valeur numerique + unite de vitesse:
 entier ou decimal SUIVI de: kms, mps, warp, swift, crawl
 ex: 42kms 3.14mps 100warp 0.5swift 1crawl
 NUMBER — nombre seul sans unite: entier ou decimal signe
 ex: 42 -7 +3.14 0.001
 IDENTIFIER — nom de sort/variable: lettre ou _ suivi de [A-Za-z0-9_]*
 OPERATOR — + - * / ^ ~
 SEPARATOR — ( ) , :
 END — ## (terminateur obligatoire de chaque instruction)
 COMMENT — // jusqu'a fin de ligne (ignore, non emis)

Contrainte C=1: chaque instruction doit commencer par un KEYWORD.
 Exclusion: SPEED n'accepte que les 5 unites definies; un nombre suivi d'un mot quelconque n'est PAS un SPEED valide.
 Repetition: NUMBER utilise [0-9]+ (Kleene +); IDENTIFIER [A-Za-z0-9_]* (Kleene *)
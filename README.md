# ASMDLabs
ASMD Labs — raccolta di esercitazioni e moduli didattici per argomenti su testing, programmazione avanzata e tecnologie LLM.

## Struttura top-level

```
asmd-public-01-atdd/             # Esempi e laboratorio su ATDD (Acceptance/Test-Driven Development)
asmd-public-02-testing/          # Laboratorio/risorse su tecniche di testing
asmd-public-03-llm-intro-code/   # Introduzione ai LLM con esempi di codice
asmd-public-models06070809/      # Esempi, esperimenti o modelli (versioni 06/07/08/09)
asmd24-public-04-advanced-programming/  # Materiale di programmazione avanzata (anno 24)
asmd25-03b-code-ai-assisted-soft-dev/   # Esercitazioni su sviluppo software assistito da AI (anno 25)
README.md
```

## Descrizione delle sottocartelle

- asmd-public-01-atdd
  - Contenuto osservato:
    - build.sbt — file di build (sbt), il progetto è configurato per essere costruito con SBT.
    - .gitignore
    - project/ — directory di configurazione del progetto sbt (sottoprogetti, plugin, ecc.).
    - src/main/java/calculator/Calculator.java — implementazione Java di una semplice classe Calculator.
    - src/test/java/calculator/ — directory per i test unitari (risorse di test in src/test/resources).
  - Scopo: modulo didattico che mostra un esempio pratico di ATDD/TDD con una semplice applicazione "calculator" (sorgente in Java, build gestita con sbt). Contiene codice sorgente e test.

- asmd-public-02-testing
  - Scopo previsto (dal nome): raccolta di esercitazioni, esempi e risorse pratiche incentrate su tecniche di testing (unit test, integrazione, strumenti e best practice).

- asmd-public-03-llm-intro-code
  - Scopo previsto (dal nome): materiali introduttivi sull'uso e l'integrazione dei Large Language Models (esempi di codice, notebook, demo).

- asmd-public-models06070809
  - Scopo previsto (dal nome): esperimenti o repository di versioni/modelli etichettati 06/07/08/09; potrebbe contenere artefatti, dati o esempi relativi a modelli.

- asmd24-public-04-advanced-programming
  - Scopo previsto (dal nome): materiale avanzato di programmazione (esercizi, soluzioni, esempi avanzati), probabilmente relativo a un corso/anno 24.

- asmd25-03b-code-ai-assisted-soft-dev
  - Scopo previsto (dal nome): laboratorio/esercitazione su sviluppo software assistito da AI (workflow, strumenti, esempi pratici), probabilmente correlato a un corso/anno 25.

## Come è organizzato il modulo `asmd-public-01-atdd`
- È un progetto sbt che contiene codice Java sotto `src/main/java` e test sotto `src/test/java`.
- Classe vista: `calculator/Calculator.java` — un esempio minimale di componente su cui fare testing.
- Ci sono risorse di test sotto `src/test/resources`.

## Come eseguire (sintesi rapida per il modulo sbt osservato)
Comandi tipici (da eseguire nella radice del modulo asmd-public-01-atdd):

```
cd asmd-public-01-atdd
sbt compile
sbt test
```

(build.sbt è presente; questi comandi assumono sbt installato)

## Note
- La descrizione dettagliata del contenuto della cartella `asmd-public-01-atdd` è basata sui file effettivamente ispezionati (build.sbt, src, Calculator.java, ecc.).
- Le descrizioni delle altre cartelle sono state fornite in base ai nomi e all'organizzazione top-level; se vuoi, posso aprire e documentare file specifici all'interno di ciascuna di esse per aggiungere dettagli precisi sui contenuti (file chiave, linguaggi usati, istruzioni di esecuzione).

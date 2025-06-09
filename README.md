# clojure-choco-solver

A Clojure project that utilizes the Choco Solver library for constraint satisfaction problems.

## TODO

- Nicer output (HTML?)
- Spårare är 24 scouter. Dela i två grupper som i sin tur kan vara två undergrupper. 6 x 2 x 2
- Spårare måste boa in sig onsdag kväll så korta kvälls-invigningen.
- Segla hem tidigt på lördagen för IF-besättningarna 

- Andra noteringar från 9 juni
  - Spårarna får effektiv lunch (t ex ombord på transport från Stavsnäs) så att
  - Tobias vill ha information kring "Ön runt" 2024.
  - Anna skickar seglingspass - gärna med stegring jämfört med 2024
  - Kl 12 hämtas scouterna. 
  - Taxibåt med lördag kommer dit kl 15 förtruppen
  - Vilka är förtrupp? Stuart, Malin,
  - Coop lördag kommer mellan 15-19 och på onsdag 
  - Lördag hem - Stuart måste veta hur många som ska åka hem så om det blir en eller två båtar
  - Fråga om parkering vid Stavsnäs - den
  - 2 släpvagnar behövs till Stuart
  - Budget till Malin
  - Allergi-listan till Malin
  - Hemseglingsmaten ska preciseras så att den kan beställas till onsdagen
  - Ditseglingsmaten sköter Anna
  - Anna ska skicka gamla dokument 
  - Anders delar länk till Google drive
  - Tobias bygger dusch om han får tid
  - Fiske pass ska Thomas
  - Sjukvårdspass Anders och Emy ordnar
  - Emy går igenom sjukvårdsväskan - resorb, värktabletter, antihistamin
  - 
 
- Motufetu tisdag kväll
  - Hur ta sig till stranden?
  - Vilken mat och dryck kommer det föras ut till Motufetu
  - 

## Project Setup

To set up this project, ensure you have Clojure and the necessary tools installed. You can then clone this repository and navigate to the project directory.

## Dependencies

This project uses the Choco Solver library for solving constraint satisfaction problems. The dependency is specified in the `deps.edn` file.

## Running the Project

To run the project, you can use the following command:

```
clj -M -m clojure_choco_solver.core
```

Make sure to replace `clojure_choco_solver.core` with the appropriate namespace if you change it.

## Choco Solver

Choco Solver is a Java library for constraint satisfaction problems. For more information, visit the [Choco Solver GitHub page](https://github.com/chocoteam/choco-solver).

### Choco resources

https://www.baeldung.com/java-constraint-programming-choco

javadoc

https://javadoc.io/doc/org.choco-solver/choco-solver/4.10.18/org.chocosolver.solver/module-summary.html


## Testing 

https://tonitalksdev.com/how-to-get-started-with-tdd-in-clojure

Run tests from commandline:

```
clojure -X:test        

# Exploding Atoms

Aplikace Exploding Atoms je implementací stejnojmenné hry pro počítače. Hra je určena pro dva hráče a je možné hrát i proti počítači.

Aplikace je naprogramována v jazyce Java s využitím knihovny Java Swing pro vytvoření uživatelského prostředí.

## Pravidla

Hra se hraje na normální šachovnici. Na každém políčku je jedno atomové jádro, které může patřit jednomu z hráčů a mohou kolem něj obíhat elektrony. Na počátku hry nepatří žádné jádro nikomu a nikde nejsou žádné elektrony. Hráči se střídají, v každém tahu jeden z nich přidá elektron k některému ze svých atomů, případně k atomu dosud neobsazenému (který si tím přivlastní). Pokud tak počet elektronů dosáhne kritického množství (to je rovno počtu sousedů daného atomu, tedy 2 pro rohové atomy až 4 pro vnitřní), atom exploduje a jeho elektrony se rozletí do všech směrů k sousedním atomům, které tak rovněž připadnou táhnuvšímu hráči a případně také explodují atd. Hra končí, přijde-li jeden z hráčů o všechny své atomy (tím prohraje). [1]

## Kompilace a spuštění programu

K překladu zdrojového kódu je zapotřebí mít k dispozici Javu verze alespoň 17. Program se snadno přeloží pomocí nástroje *Maven*:

TODO

## Ovládání hry

Po spuštění programu se zobrazí okno aplikace s hlavním menu. V něm můžete vybrat typ každého hráče, na výběr je *Human* (člověk) a *Computer* (počítač). Po stisknutí tlačítka *Play* začne hra. V levé části okna je zobrazena šachovnice a v pravé části stav hry. Hráč, který je na řadě, může kliknutím zvolit políčko, na které chce umístit elektron. Hru můžete kdykoliv ukončit pomocí tlačítka *Quit*.

## Vývojová dokumentace

Dokumentace kódu může být vygenerována pomocí nástroje *javadoc*:

TODO

## Důkazy

Některé zvolené postupy vyžadovaly matematické důkazy o průběhu hry.

### Zacyklení explozí

Ve hře dochází k řetězení explozí, které nemusí nikdy skončit. Můžeme si například představit šachovnici, která je plně obsazena elektrony obou hráčů, a následující hráč položí další elektron. Tím začne posloupnost explozí, která nikdy neskončí, protože počet elektronů se nikdy nesnižuje a jeden přebývá.

V původních pravidlech by se měl takový nekonečný cyklus detekovat a vyhlásit remíza. Bohužel jsem nepřišel na to, jak toto dělat jiným způsobem, než po jednotlivých fázích explozí kontrolovat, zda se nějaká fáze neopakuje. To by značně zpomalilo program při vyhodnocování tahů v algoritmu Minimax. Podařilo se mi ale dokázat, že pokud k nekonečnému cyklení dojde, musí být explozemi nutně zabrány políčka druhého hráče. Díky tomu jsem mohl upravit pravidla tak, že vítězí ten hráč, který nejdříve obsadí všechna políčka druhého hráče. Počet elektronů hráčů lze v programu snadno kontrolovat a tah může být ukončen předčasně, tedy nedochází k zacyklení.

**Tvrzení**: Pokud hráč svým tahem způsobí nekonečné řetězení explozí, pak musí tímto tahem zabrat všechna políčka druhého hráče.

**Důkaz (náznak)**: Na šachovnici je pouze konečný počet políček, na kterých k explozím dochází. Protože je explozí nekonečně mnoho, tak políčka, která explodují, zaberou své sousedy a donekonečna je zásobují elektrony. Z toho důvodu se i tito sousedé musí jednou zaplnit a budou nekonečněkrát explodovat. Indukcí se tímto způsobem dostanou exploze na všechna políčka, tedy druhý hráč o přišel o všechny své elektrony.



TODO: může se do fronty explozí dostat to samé políčko dvakrát? vadí to?
- možná druhý důkaz zaručuje, že se toto nestane?


// Mathematical proofs:
// 1. If there is endless loop of explosions, the player who caused it will take over all enemy electrons
// Proof:
//   By contradiction. Let's assume player causes an endless loop and an enemy square will be left untouched.
//   Let's take the boundary inside which the explosion of the loop happen and outside not. The number of squares
//   neighboring the boundary is finite. Every time explosion happens inside the boundary, an electron will be sent
//   outside. Thus squares neighboring the boundary have an endless supply of electrons, so they must explode too at
//   one point. Hence, we have the contradiction with the fact that atoms outside the boundary do not explode.
// Consequence: We can stop the game when all squares belong to 1 player
//
// 2. Two neighboring atoms cannot explode at the same time
// Proof:
//   Explosions happen in waves and are initiated with 1 explosion, when we put 1 electron to the board.
//   Let's say we put this electron to white square. Then in the next wave, the explosions can be initiated only from
//   black squares. In the following wave, the explosions can be initiated only from the white squares, etc.
//   Thus, each wave of explosions is initiated only from white squares or only from black squares.
//   2 neighbors cannot explode in the same wave, because they have different square colors.


## Reference

[1] http://mj.ucw.cz/vyuka/zap/
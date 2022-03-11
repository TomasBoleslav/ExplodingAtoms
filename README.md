# Exploding Atoms

Aplikace Exploding Atoms je implementací stejnojmenné hry pro počítače. Hra je určena pro dva hráče a je možné hrát i proti počítači.

Aplikace je naprogramována v jazyce Java s využitím knihovny Java Swing pro vytvoření uživatelského prostředí.

## Pravidla

Hra se hraje na normální šachovnici. Na každém políčku je jedno atomové jádro, které může patřit jednomu z hráčů a mohou kolem něj obíhat elektrony. Na počátku hry nepatří žádné jádro nikomu a nikde nejsou žádné elektrony. Hráči se střídají, v každém tahu jeden z nich přidá elektron k některému ze svých atomů, případně k atomu dosud neobsazenému (který si tím přivlastní). Pokud tak počet elektronů dosáhne kritického množství (to je rovno počtu sousedů daného atomu, tedy 2 pro rohové atomy až 4 pro vnitřní), atom exploduje a jeho elektrony se rozletí do všech směrů k sousedním atomům, které tak rovněž připadnou táhnuvšímu hráči a případně také explodují atd. Hra končí, přijde-li jeden z hráčů o všechny své atomy (tím prohraje). [1]

## Kompilace a spuštění programu

K překladu zdrojového kódu je zapotřebí mít k dispozici Javu verze alespoň 17. Program se snadno přeloží pomocí nástroje *Maven*:

```shell
$ mvn package
```

Spuštění programu:

```shell
$ java -jar target/atoms-1.0.jar
```

## Ovládání hry

Po spuštění programu se zobrazí okno aplikace s hlavním menu. V něm můžete vybrat typ každého hráče, na výběr je *Human* (člověk) a *Computer* (počítač). Po stisknutí tlačítka *Play* začne hra. V levé části okna je zobrazena šachovnice a v pravé části stav hry. Hráč, který je na řadě, může kliknutím zvolit políčko, na které chce umístit elektron. Hru můžete kdykoliv ukončit pomocí tlačítka *Quit*.

## Vývojová dokumentace

Dokumentace kódu může být vygenerována pomocí nástroje *javadoc*:

TODO

### Zacyklení explozí

Ve hře dochází k řetězení explozí, které nemusí nikdy skončit. Můžeme si například představit šachovnici, která je plně obsazena elektrony obou hráčů, a následující hráč položí další elektron. Tím začne posloupnost explozí, která nikdy neskončí, protože počet elektronů se nikdy nesnižuje a jeden přebývá.

V původních pravidlech by se měl takový nekonečný cyklus detekovat a vyhlásit remíza. Bohužel jsem nepřišel na to, jak toto dělat jiným způsobem, než po jednotlivých fázích explozí kontrolovat, zda se nějaká fáze neopakuje. To by značně zpomalilo program při vyhodnocování tahů v algoritmu Minimax. Podařilo se mi ale dokázat, že pokud k nekonečnému cyklení dojde, musí být explozemi nutně zabrány políčka druhého hráče. Díky tomu jsem mohl upravit pravidla tak, že vítězí ten hráč, který nejdříve obsadí všechna políčka druhého hráče. Počet elektronů hráčů lze v programu snadno kontrolovat a tah může být ukončen předčasně, tedy nedochází k zacyklení.

**Tvrzení**: Pokud hráč svým tahem způsobí nekonečné řetězení explozí, pak musí tímto tahem zabrat všechna políčka druhého hráče.

**Důkaz (náznak)**: Na šachovnici je pouze konečný počet políček, na kterých k explozím dochází. Protože je explozí nekonečně mnoho, tak políčka, která explodují, zaberou své sousedy a donekonečna je zásobují elektrony. Z toho důvodu se i tito sousedé musí jednou zaplnit a budou nekonečněkrát explodovat. Indukcí se tímto způsobem dostanou exploze na všechna políčka, tedy druhý hráč o přišel o všechny své elektrony.

## Reference

[1] http://mj.ucw.cz/vyuka/zap/
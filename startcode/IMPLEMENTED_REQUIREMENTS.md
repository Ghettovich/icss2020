### READ ME
Helaas transformatie en generen niet afgekregen, maar zou graag willen weten of de ik aan de algemene eisen voldoe (met name AL03) en op de goede weg zit.
In de tabellen met een X aangegeven wat is gedaan.

### 4.1 Algemene eisen (0 punten)
De code die je oplevert is een uitbreiding op de bij de opdracht beschikbaar gestelde startcode. Voor de code gelden de volgende algemene eisen.

ID  |Omschrijving|Prio |Punten |Voldoet?
----|--------------------------------------------------------------------|------|------|-------
AL01|De code behoudt de packagestructuur van de aangeleverde startcode. Toegevoegde code bevindt zich in de relevante packages. |Must  |0 |?
AL02|Alle code compileert en is te bouwen met Maven 3.6 of hoger, onder OpenJDK 13. Tip: controleer dit door **eerst** ```mvn clean``` uit te voeren alvorens te compileren en in te leveren. **Gebruik van Oracle versies van Java is uitdrukkelijk niet toegestaan**.   |Must  |0 |?
AL03|De code is goed geformatteerd, zo nodig voorzien van commentaar, correcte variabelenamen gebruikt, bevat geen onnodig ingewikkelde constructies en is zo onderhoudbaar mogelijk opgesteld. (naar oordeel van docent)  |Must  |0 |?

### 4.2 Parseren (40 punten)

ID  |Omschrijving|Prio |Punten | Gedaan
----|--------------------------------------------------------------------|------|------|-----
PA01|Implementeer een parser plus listener die AST’s kan maken voor ICSS documenten die “eenvoudige opmaak” kan parseren, zoals beschreven in de taalbeschrijving. In `level0.icss` vind je een voorbeeld van ICSS code die je moet kunnen parseren.  `testParseLevel0()` slaagt.|Must  |10|X
PA02|Breid je grammatica en listener uit zodat nu ook assignments van variabelen en het gebruik ervan geparseerd kunnen worden. In `level1.icss` vind je voorbeeldcode die je nu zou moeten kunnen parseren. `testParseLevel1()` slaagt.|Must  |10| X
PA03|Breid je grammatica en listener uit zodat je nu ook optellen en aftrekken en vermenigvuldigen kunt parseren. In `level2.icss` vind je voorbeeld- code die je nu ook zou moeten kunnen parseren. `testParseLevel2()` slaagt.|Should|10 | X
PA04|Breid je grammatica en listener uit zodat je if-statements aankunt. In `level3.icss` vind je voorbeeldcode die je nu ook zou moeten kunnen parseren. `testParseLevel3()` slaagt.|Should|10

### 4.3 Checken (20 punten)
Tijdens het compileren van ICSS naar CSS willen we eerst controleren of de code naast syntactisch correct, ook semantisch correct is. Dit doe je door de checker component (`nl.han.ica.icss.checker.Checker`) te implementeren. Als je fouten detecteert in de AST kun je deze in de knopen van de AST opslaan met de
`setError` methode.

Per CH onderdeel kun je 0 of de aangegeven punten krijgen.

ID  |Omschrijving|Prio |Punten | Gedaan
----|--------------------------------------------------------------------|------|------|-------
CH00|Minimaal drie van onderstaande checks **moeten** zijn geïmplementeerd|Must|0|X
CH01|Controleer of er geen variabelen worden gebruikt die niet gedefinieerd zijn.|Should|	4|X
CH02|Controleer of de operanden van de operaties plus en min van gelijk type zijn en dat vermenigvuldigen enkel met scalaire waarden gebeurt. Je mag geen pixels bij percentages optellen bijvoorbeeld.|Should|4|X
CH03|Controleer of er geen kleuren worden gebruikt in operaties (plus, min en keer).|Should|2|X
CH04|Controleer of bij declaraties het type van de value klopt met de property. Declaraties zoals width: `#ff0000` of `color: 12px` zijn natuurlijk onzin.|Should|2|X
CH05|Controleer of de conditie bij een if-statement van het type boolean is (zowel bij een variabele-referentie als een boolean literal)|Should|4|
CH06|Controleer of variabelen enkel binnen hun scope gebruikt worden|Should|4|X

Als je deze deeleisen geïmplementeerd hebt, kan je nu ook controleren of de input ICSS semantisch klopt. Voor de volgende fases in de compiler kun je er dus van uit gaan dat je met een correcte AST en gevulde symboltable verder kunt werken.

### 4.4 Transformeren (20 punten)
Om het genereren van de code makkelijker te maken gaan we de AST in een aantal stappen vereenvoudigen. Hiervoor zijn een tweetal transformaties gedefinieerd in `nl.han.ica.icss.transform`.
 
Per TR onderdeel kun je 0, 5 of 10 punten krijgen.

ID  |Omschrijving|Prio |Punten |Gedaan | 
----|--------------------------------------------------------------------|------|------|-------
TR01|Implementeer de `EvalExpressions` transformatie. Deze transformatie vervangt alle `Expression` knopen in de AST door een `Literal` knoop met de berekende waarde. |Should|10 |X
TR02|Implementeer de `RemoveIf `transformatie. Deze transformatie verwijdert alle `IfClause`s uit de AST. Wanneer de conditie van de `IfClause` `TRUE` is wordt deze vervangen door de body van het if-statement. Als de conditie `FALSE` is dan verwijder je de `IfClause`volledig uit de AST.|Should|10

### 4.5 Genereren (10 punten)
De laatste stap is het generereren van CSS2-compliant code vanuit ICSS code. Dit doe je door een tree- traversal op de volledig getransformeerde AST te doen om een string op te bouwen. 

Per GE onderdeel kun je 0 of 5 punten krijgen.

ID  |Omschrijving|Prio |Punten |Gedaan
----|--------------------------------------------------------------------|------|------|-----
GE01|Implementeer de generator in nl.han.ica.icss.generator.Generator die de AST naar een CSS2-compliant string omzet.|Must|5 | X
GE02|Zorg dat de CSS met twee spaties inspringing per scopeniveau  gegenereerd wordt.|Must|5 | X
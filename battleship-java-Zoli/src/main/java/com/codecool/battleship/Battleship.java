package com.codecool.battleship;

import java.util.Random;
import java.util.Scanner;

public class Battleship {
    private static  Object[][][] allShips; //globális 3d tömb, ami a hajó objektumokat és a hozzájuk kapcsolódó koordinátákat tárolja. Kinézete miután fel lett töltve(placement függyvényben töltjük fel) adatokkal pl:[[[P1object1, A1A2A3],[P1object2, B1B2B3],[P1object3, C1C2C3]], [[P2object1, A1A2A3],[P2object2, B1B2B3],[P2object3, C1C2C3]]]

    //getter
    public static Object[][][] getAllShips() {
        return allShips;
    }
    //setter
    public static void setAllShips(Object[][][] allShips) {
        Battleship.allShips = allShips;
    }

    public static String input(){// input bekérése és returnülése stringként
        Scanner keyboardInput = new Scanner(System.in);
        return keyboardInput.nextLine();
    }

    public static int sunkedShips1 = 0;//player1 elsüllyeszett hajói
    public static int sunkedShips2 = 0;//player2 elsüllyeszett hajói

    public static String[][] generateBoard(int size){// legenerál egy pályát size alapján, hossza mindig size+2 lesz, mert bele kell számítani a fejlécet és az oldalsó betűs oszlopot, valamint kell a végére egy láthatatlan oszlop, valamint egy láthatatlan sor(ez a pozició validálás miatt fontos, különben invaidként értelmezné, ha a pálya szélére akarnánk hajót rakni)
        String[][] board = new String[size+2][size+2];
        for(int i=0; i<board.length; i++){
            for(int j=0; j<board[i].length; j++){
                if(i == 0){ // fejléc
                    if (j != 0){
                        board[i][j]= Integer.toString(j);
                        if(j == board.length-1){
                            board[i][j] = " ";
                        }
                    }else{ board[i][j] = " ";}
                } else if(i < board.length-1) {
                    if (j == 0){
                        board[i][j] = Character.toString((char)(i+64));// HTML kód átalakítása chararcterré, majd stringé.
                    }else {
                        board[i][j] = "0";
                        if(j == board.length-1){
                            board[i][j] = " ";
                        }
                    }
                }else{
                    board[i][j] = " ";
                }
            }
        }
        return board;//visszaadja a legenerált "üres" pályát
    }

    public static void Printer(String[][] board){// bármilyen 2d tömböt(amit átadunk neki argumentumként), vagyis egy táblát printel ki
        for (int i=0; i<board.length; i++){
            for (int j=0; j<board[i].length; j++){
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    //Placement metódus lényege: kap egy üres pályát, megkapja a hajókészletet és azt az infót, hogy melyik játékos köre van éppen
    //Célja: az üres pályát feltöltse "X"-ekkel, ahova a user tenni szeretné a hajóit és adja vissza ezt a megváltoztatott - x-ekkel feltöltött - pályát
    public static void Placement(String[][] board, int[] shipsPc, int player){ //shipsPC = [3,1,2] -> 3db 1es hajó, 1db 2es hajó, 2db 3mas hajó
        System.out.println(System.lineSeparator().repeat(50));
        System.out.println("Placement phase! Player " + player + "'s turn! Press any key and then enter to continue!");
        input();//Press any key to continue megvalósításához kell - addig nem megy tovább a kód, amíg be nem ír valamit a user inputként
        String posInput;
        Object[][][] innerShipProp = getAllShips();//egy lokális tömbbe elmentjük a globális 3d tömbünket, amit majd ebben a fügvényben adatokkal töltünk fel és a végén egy setter visszadjuk neki
        int whichShip = -1; // ez a változó fogja számolni, hogy éppen hányadik hajót akarjuk lerakni
        for (int i=0; i<shipsPc.length; i++){ // Belemegyünk a hajókészletbe, ha i=0 akkor az 1-es hosszúságú hajóknál tartunk, ha i=1, akkor a 2-es és így tovább
            for (int j=0; j < shipsPc[i]; j++){ //A shipPc i-edik indexén mindig egy szám van, ami azt jelzi, hogy az adott hosszúságú hajóból hány darab van a készletben, az összes hajón egyesével végigmegyünk a j segítségével
                whichShip++;//növeljük egyel, hogy éppen melyik hajónál vagyunk
                while (true){//ez a ciklus arra szolgál, hogy addig fusson, amíg valid inputot nem adott nekünk a user
                    Printer(board);//a magkapott pálya éppen aktuális állapotát magmutatjuk
                    System.out.println("Place one of your " + (i+1) + " length ship!");
                    System.out.println("You have: " + (shipsPc[i]-j) + " ship(s) left." );
                    System.out.println("Enter position: (Format: VA1, HA1)");
                    posInput = input();//bekérjük az inputot a usertől pl VA1
                    if (validateInput(posInput, "placement", board.length-1)){ //Ha az input formátum rendben van - ennek ellenúrzésére a validateinput függvényt hívjuk meg és ha true-t ad visza akkor meggyünk tovább az if alatt
                        Object[] tempArray = cut(posInput); //Meghívjuk a cut függvényt, ami a user inputját feldarabolja nekünk (pl VA1-et feldarabolja V 1 1-re(A-t átalakítja számra)) és ezt eltároljuk egy tömbbe, ahol az 0-ik index lesz egyenlő az iránnyal pl V, az első index az y koordinátát adja meg pl 1(ami pl az A betű átalakírtásával kaptunk) a második index pedig az x koordinátával lesz egyenló
                        if (validatePosition((String) tempArray[0],(int)tempArray[1], (int)tempArray[2], board, i+1)){ //Megnézzük a koordinátákat, hogy megfelelőek-e -> lásd journey az elhelyezési szbályokra,ehhez a validateposition függvényt hívjuk meg, és ha az true-t ad vissza megyünk tovább a kóddal
                            innerShipProp[player-1][whichShip][0] = new Ship(i+1); //3d tömbünket feltöltjük objektumokkal, i+1-> hány egység hosszú a hajó, ezt a ship class constructorának átadjuk. Tömb felépítése: először 2 részre ágazik - egy player1 és egy player2 részre, majd ezek is annyi részre ahány hajónk van a hajókészletben, majd minden hajónak van egy koordinátája és egy objektum referenciája - így fog kinézni, ez a cél: [[[P1object1, A1A2A3],[P1object2, B1B2B3],[P1object3, C1C2C3]], [[P2object1, A1A2A3],[P2object2, B1B2B3],[P2object3, C1C2C3]]]
                            Ship ship = (Ship)innerShipProp[player-1][whichShip][0];//A tömbből kijelöljük a fent berakott hajó objektumot, ezt elmentjük egy Ship típusú változóba (mivel objektumról van szó - csak saját class típusú változóba lehet menteni)
                            int[][] shipCoordinate = ship.getCoordinate();//lehívjuk a kijelült hajó objektum koordinátátit egy getter-el (ez egy üres nullokkal feltöltött 2d tömb lesz), ezt egy lokális 2d tömbbe elmentjük és ezt fogjuk később feltölteni koordináta párokkal
                            for (int k=0; k < i+1; k++){ // ennek a ciklusnak a lényege abban fogható meg, hogy ha 1-nél szélesebb hajót akarunk lerakni, akkor nem csak a user által megadott inputot kell koordinátaként lementeni, hanem annyi x-y koordináta pár lesz, amilyen széles a hajó, tehát i+1-ig megy a ciklus, azaz a hajó hosszáig(i+1 a hajó hossza)
                                //különbséget kell tenni direction-ok között
                                if (tempArray[0].equals("H")){ //Ha Horizontális
                                    innerShipProp[player-1][whichShip][1] += Character.toString((char)((int)tempArray[1]+64)) + ((int)tempArray[2]+k); //Felépítjük a koordinátát. Ahogy korábban írtam a 3d tömbünk így fog kinézni, ez a cél: [[[object1, A1A2A3],[object2, B1B2B3],[object3, C1C2C3]]]. Vagyis most pl A1A2A3-al akarjuk feltölteni, ami egy string (ezek a koordináták alkotják a hajót), whichsjip jelzi, hogy éppen melyik hajónál tartunk
                                    board[(int)tempArray[1]][(int)tempArray[2]+k] = "X"; //Ykooridnáta: (sor) változatlan, Xkoordináta (oszlop):annyiszor megyünk 1 egységet jobbra, ahányszor iterál a k, tehát amilyen hosszú az adott hajó, és X-eket rakunk le a pályára
                                    shipCoordinate[k][0] = (int)tempArray[1];//updateljük a shipCoordinate 2d tömbünket, ahogy korábban írtam ezt fogjuk majd egy setter-el visszaadni az éppen kijelült hajó objektumnak. Ez a tömb x-y koordinátapárokat tartalmaz, méghozzá annyit amennyi hosszú a hajó
                                    shipCoordinate[k][1] = (int)tempArray[2]+k;//updateljük a shipCoordinate 2d tömbünket, ahogy korábban írtam ezt fogjuk majd egy setter-el visszaadni az éppen kijelült hajó objektumnak. Ez a tömb x-y koordinátapárokat tartalmaz, méghozzá annyit amennyi hosszú a hajó
                                }else {//Ha vertikális
                                    innerShipProp[player-1][whichShip][1] += Character.toString((char)((int)tempArray[1]+64+k)) + (int)tempArray[2]; //Felépítjük a koordinátát
                                    board[(int)tempArray[1]+k][(int)tempArray[2]] = "X"; // Ugyanaz mint a vízszintes, csak itt az oszlop statikus, a sor viszont iterálódik k val.
                                    shipCoordinate[k][0] = (int)tempArray[1]+k;//updateljük a shipCoordinate 2d tömbünket, ahogy korábban írtam ezt fogjuk majd egy setter-el visszaadni az éppen kijelült hajó objektumnak. Ez a tömb x-y koordinátapárokat tartalmaz, méghozzá annyit amennyi hosszú a hajó
                                    shipCoordinate[k][1] = (int)tempArray[2];//updateljük a shipCoordinate 2d tömbünket, ahogy korábban írtam ezt fogjuk majd egy setter-el visszaadni az éppen kijelült hajó objektumnak. Ez a tömb x-y koordinátapárokat tartalmaz, méghozzá annyit amennyi hosszú a hajó
                                }
                            }
                            ship.setCoordinate(shipCoordinate);//az kész shipCoordinate 2d tömböt visszaadjuk egy setter-el a hajó objektumunknak (ami a ship nevű változóban van)
                            break;//ha mindennek a végére ért, minden tömböt updatelt, kiugrunk a while ciklusból és jöhet a következő hajó a készletből
                        }
                    }
                } //while vége
            } //második for vége
        }//első for vége
    }

    public static Boolean validateInput (String input, String type, int size){ //type: size (pályaméret), placement(koordináta), else ág - shooting koordináta
        //viszgáljuk h input formátumok megfelelőek-e
        if (type.equals("size")){
            try {
                int inputSize = Integer.parseInt(input);
                if(inputSize >= 5 && inputSize <= 10){
                    return true;
                }
                System.out.println("Invalid input! (must be between 5-10)");
                return false;

            }catch (Exception e){
                System.out.println("Invalid input!");
            }
        }else if (type.equals("placement")){
            if ((Character.toString(input.charAt(0)).equalsIgnoreCase("V") ||
                    Character.toString(input.charAt(0)).equalsIgnoreCase("H")) &&
                    (int)input.charAt(1) >= 65 && (int)input.charAt(1) <= 74 &&
                    Integer.parseInt(input.substring(2)) <= size && Integer.parseInt(input.substring(2)) > 0) {
                return true;
            }
        }else{
            try{
                if ((int)input.charAt(0) >= 65 && (int)input.charAt(0) <= 74 && Integer.parseInt(input.substring(1))>=1 && Integer.parseInt(input.substring(1))<=10 ) {
                    return true;
                }
            }catch (Exception e){
                System.out.println("Invalid input!");
            }
        }
        System.out.println("Invalid input!");
        return false;
        //else ág placement validálásához.
    }

    public static Boolean validatePosition (String dir, int posY, int posX, String[][] board, int shipSize ){ //HA10, VA2
        /*Így kell elképzelni a validálást:
        * Tegyük fel, hogy a hajó 3 koordináta hosszú és a horizontálisan a C2-re helyztük. I-vel és X-el(ahol a hajó lenne) van jelezve, hogy melyek az invalid poziciők, vagyis hol nem subad X-eknek lenni, ezeket kell viszgálni
        * Egy for ciklussal lehet ezt megoldani, ami a C1-ról indul, első lépésnél csak a ahjó sorát nézi - csak a C1 koordinátát, majd tovább lépéseknél a hajó sorát és az alatti és feletti sorát - vagyis pl második lépésnél a C2-t, B2-t éa D2-t - és így tovább egészen addig amíg el nem érünk  a példánk szerint az 5. lépésig, ahol szinttén csak a hajó sorát nézni, a C5-öt
        *  1 2 3 4 5
        *A 0 0 0 0 0
        *B 0 I I I 0
        *C I X X X I
        *D 0 I I I 0
        *E 0 0 0 0 0
        *
        * */
        try {// try azért kell mert pl a 3 hosszúságú hajót C4-re helyznénk horizontálisan -> kilógna a pályáról és egy exception-t adna vissza
            if (dir.equalsIgnoreCase("H")){
                for (int i = -1;i <= shipSize; i++){ // itt ne vegyük figyelembe a koordinátát, csak a ciklusokat.
                    if (i == -1 || i == shipSize){
                        if(board[posY][posX + i].equals("X")){
                            System.out.println("Invalid input!");
                            return false;
                        } // hajó sora
                    }else{
                        if(board[posY][posX + i].equals("X") || board[posY - 1][posX + i].equals("X") || board[posY + 1][posX + i].equals("X")){
                            System.out.println("Invalid input!");
                            return false;// mind a 3 sor
                        }
                    }
                }
            }else {
                for (int i = -1;i <= shipSize; i++){ // itt ne vegyük figyelembe a koordinátát, csak a ciklusokat.
                    if (i == -1 || i == shipSize){
                        if(board[posY + i][posX].equals("X")){
                            System.out.println("Invalid input!");
                            return false;
                        } // hajó sora
                    }else{
                        if(board[posY + i][posX].equals("X") || board[posY + i][posX - 1].equals("X") || board[posY + i][posX + 1].equals("X")){
                            System.out.println("Invalid input!");
                            return false;//mind a 3 sor
                        }
                    }
                }
            }
        }catch (Exception e){// ha exceptiont ad vissza
            System.out.println("Invalid input!");
            return false;
        }
        return true;
    }

    public static Object[] cut (String beforeCut){//beforeCut pl VA1
        Object[] tempArray = new Object[3];
        tempArray[0] = String.valueOf(beforeCut.charAt(0));//pl V
        tempArray[1] = (int)(beforeCut.charAt(1))-64;// pl: 1 -> pl A betűt átalakítottuk
        tempArray[2] = Integer.parseInt(beforeCut.substring(2));// pl: 1
        return tempArray;// tempArray tartalma pl: [V, 1, 1]
    }

    static int[] generateShips (int size){ // ship[]
        int[] ships = new int[4]; //0index: hány db 1s hosszúságú, 1index: hány db 2s hosszúságú, 2index: hány db 3mas hosszúságú...
        switch (size){
            case 5:
                ships[0] = 2; // 2 db 1es hajó
                ships[1] = 2; // 2 db 2es hajó
                break;
            case 6:
                ships[0] = 3; // 1es hajó
                ships[1] = 2; // 2es hajó
                break;
            case 7:
                ships[0] = 3; // 1es hajó db
                ships[1] = 2; // 2es hajó
                ships[2] = 1; // 3as hajó
                break;
            case 8:
                ships[0] = 2; // 1es hajó db
                ships[1] = 1; // 2es hajó
                ships[2] = 1; // 3as hajó
                ships[3] = 2; // 4es hajó
                break;
            case 9:
                ships[0] = 3; // 1es hajó db
                ships[1] = 2; // 2es hajó
                ships[2] = 2; // 3as hajó
                ships[3] = 1; // 4es hajó
                break;
            case 10:
                ships[0] = 3; // 3 db egyes hajó
                ships[1] = 2; // 2 db kettes hajó
                ships[2] = 2; // 2 db hármas hajó
                ships[3] = 2; // 2 db négyes hajó
                break;
        }
        return ships;
    }

    public static String[][][] shooting(String[][] shooting, String[][] placement, int player, int shipSum){
        System.out.println("It's shooting phase! Player " + player + "'s turn! Press any key to continue!");
        input();//várakozás miatt
        String[][][] updatedBoards = new String[2][shooting[0].length][shooting[0].length];//létrehozzuk azt a 3d tömböt amit majd vissza akarunk adni a main-nek - az updatelt shootung és placement táblákat fogja tartalmazni
        while(true){//addig fut a ciklus amíg valid nem lesz a user inputja
            Printer(shooting);// kiírjuk a shooting táblát
            System.out.println("Where do you want to shoot? Format:A1");
            String shootInput = input();//bekérjük az inputot a usertől pl A1
            if(validateInput(shootInput, "shooting", 0)){//ha valid az input formátuma
                int Y = (int) shootInput.charAt(0) - 64;//y koordináta számra alakítása
                int X = Integer.parseInt(shootInput.substring(1));//x koordináta átalakítása integerré
                if (placement[Y][X].equals("X")){//ha a user által megadott koordináta helyén X van a másik player placement tábláján
                    placement[Y][X] = "H";//írjuk H-ra az X-et placement táblán
                    shooting[Y][X] = "H";//írjuk H-ra az X-et shooting táblán
                    Object[][][] shipProp = getAllShips();//lekérjük a 3d globális tömbünket és egy lokális 3d tömbben eltároljuk (ugye ebbe tároljuk a hajó objektumokat és a melléjük rendelt koordináta stringet - így néz ki pl a tartalma: [[[object1, A1A2A3],[object2, B1B2B3],[object3, C1C2C3]]]), azért object típusú, mert vegyes data typeokat tartalmaz
                    for(int i=0; i<shipProp[player-1].length; i++){//végig interálunk a fent ismertett tömb elemein
                        String Coordinate =(String)shipProp[player-1][i][1];//Ahogy haladunk a ciklussal a koordináta stringet(pl:A1A2A3) elmentjük egy változóba, ez az i-betűvel folyamatosan változik
                        if(Coordinate.contains(shootInput)){// az eltárol koordinát stringnél megnézzük, hogy a user által megadott inputot tartalmazza-e pl user tippje: A1 - koordináta stringünk: A1A2a3 -> ez tartalmazza az A1-et vagyis megvan hogy melyik hajót találtuk el (koordinát string mellett ott van a hajó objektum referencia is)
                            Ship ship =(Ship)shipProp[player-1][i][0];//kinyerjük az eltalált hajó objektumot és elmentjük ship nevű Ship tíőpusű változóba (0-ik index tartalmazza az obejktum referenciát, a 1-es pedig a koordináta stringet)
                            int shipSize = ship.getSize();// lekérjük egy getterel a kijelölt(eltalált) hajó életét(ami alapból a szélessségel egynelő)
                            ship.setSize(shipSize-1);//az életéből levonunk 1-et és ezt az értéket visszaadjuk neki egy setter-el
                            if (shipSize-1 == 0){// azt viszgáljuk ha a levonás után a hajó élete 0 lesz -> vagyis valamennyi koordinátáját eltalálták
                                int[][] shipCoord = ship.getCoordinate();//lekérjük az összes koordináta párját a kijelölt hajónknak(ez egy 2d tömb ami így néz ki pl: [[1,1][,1,2]])
                                for (int j=0; j<shipCoord.length; j++){//végig iterálunk a koordináta párok tartalmazó tömbön
                                    shooting[shipCoord[j][0]][shipCoord[j][1]] = "S";// 0-ik index jelzi az y koordinátát, 1-es index pedig az x-et, és ezeket a koordinátákat updateljük a shooting táblán -> H betűket S-ra változtatjuk
                                    placement[shipCoord[j][0]][shipCoord[j][1]] = "S";// 0-ik index jelzi az y koordinátát, 1-es index pedig az x-et, és ezeket a koordinátákat updateljük a placement táblán -> H betűket S-ra változtatjuk
                                }
                                System.out.println("You sunk a ship!");//Ha mindent updateltünk kiírunk
                                getWinner(player, shipSum);// megvizsgáljuk a getWinner függvénnyel, hogy a hajó amit kilőttünk az az utolsó hajó volt-e, ha igen a getWinner függvény befejezi a játékot és győztets hirdet, vagy döntetlent ír ki
                            }else{// ha a hajó élete nagyobb mint 0, akkor nem írjuk át S-ra a H-kat
                                System.out.println(("You hit a ship!"));//kiírjuk h csak eltalálta a hajót
                            }
                        }
                    }
                }else {// ha nem X-et találtunk el akkor kiírjuk h miss volt
                    shooting[Y][X] = "M";
                    System.out.println("You missed!");
                }
                break;// ha mindent updateltünk kiugrunk a while ciklusból és megyünk tovább a kóddal
            }
        }

        updatedBoards[0] = shooting;//korábban említve volt, hogy egy 3d tömböt adunk vissza, ami tartalmazza majd az updatelt shooting és placement 2d tömböket - ezzel a 3 sorral ezt valósítjuk meg
        updatedBoards[1] = placement;
        return updatedBoards;
    }

    public static void getWinner(int player, int shipSum){
        if(player == 1){
            sunkedShips1++;//player1 elsüllyeszett hajóit növeljük
        }else{
            sunkedShips2++;//player2 elsüllyeszett hajóit növeljük
        }
        if(sunkedShips1 == shipSum && player == 2 && sunkedShips2 != shipSum){//győzelmek, döntetlenek feltételei
            System.out.println("Player 1 won!!!");
            System.exit(0);
        }else if(sunkedShips2 == shipSum && sunkedShips1 != shipSum){
            System.out.println("Player 2 won!!!");
            System.exit(0);
        }else if(sunkedShips1 == shipSum && sunkedShips2 == shipSum){
            System.out.println("It's a draw!!!");
            System.exit(0);
        }
    }

    public static void main(String[] args) {

        int shipSum = 0;
        String size;
        while (true){// addig megy a cilkus amíg nem adunk meg valid size értéket
            System.out.println("Enter size: ");
            size = input();
            if (validateInput(size,"size", 10)){
                break;
            }
        }
        int intSize = Integer.parseInt(size);// az inputból visszakapott size stringet int-re alakítjuk
        int[] shipCounter = generateShips(intSize);//a size alapján legeneráljuk a hajókészletet

        for(int i=0; i<shipCounter.length; i++){shipSum += shipCounter[i];}// megszámoljuk hány hajónk van összesen
        setAllShips(new Object[2][shipSum][2]); //a globális 3d tömbünket létrehozzuk egy setter-el, azért itt, mert a méretei függnek a hajók számától
        String [][][] boards = new String[4][intSize][intSize]; //boards eltárolja a placement és shooting 2d tömböket(táblákat) - 4 ilyen tábla lesz
        int x = 0;

        while (x < 4){ // legenerálja 4x a táblát, és eltároljuk a boards változóban a négy táblát.
            // boards[0]:első tábla...
            boards[x] = generateBoard(intSize);
            x++;
        }// boards[0] lesz a player1 placement táblája, boards[1] a player2 placement táblája, boards[2] a player1 shooting táblája, boards[3] pedig a player2 shooting táblája
        for (int i=1; i<3; i++){Placement(boards[i-1], shipCounter, i);}//updateljük mind a 2 placement táblát(feltöljük hajókkal(X-ekkel)), i fogja jelülni melyik játékos köre van

        int turn = 0;
        while (true){// shooting fázis menete, hogy körönként váltogasság egymást a játékosok
            if (turn % 2 == 0){ //Player1 köre: turn páros Player2 köre: turn páratlan (moduloval(%) tudjuk ezt viszgálni)
                String[][][] tempPlayer1 = shooting(boards[2], boards[1], 1, shipSum);// shooting függvénynek mindig átadjuk a player1 shooting tábláját, a player2 placement tábláját, melyik játékos köre van éppen és a hajók számát(győzelmet, döntetlent ezzel viszgálja majd - ehhez fogja viszonyítani a elsüllyeszett hajók számát)
                // tempPlaser1-be kapjuk vissza mindig az updatelt shooting és placement táblákat
                tempPlayer1[0] = boards[2];//az updatelt táblákat átadjuk a boards tároló tömbünknek(updateljük)
                tempPlayer1[1] = boards[1];//az updatelt táblákat átadjuk a boards tároló tömbünknek(updateljük)
                //így tehát mindeh amikor páros a turn, updatelt placement és shooting táblákat adunk a shooting függvénynek
            }else {// egynaz mint az előző, csak ide akor megyünk be, ah a turn páratlan(player 2 ilyenkor következik)
                String[][][] tempPlayer2 = shooting(boards[3], boards[0], 2, shipSum);
                tempPlayer2[0] = boards[0];
                tempPlayer2[1] = boards[3];
            }
            turn++;
        }
    }
}
package edu.uob;



public class OXOController {
    OXOModel gameModel;

    public OXOController(OXOModel model) {
        gameModel = model;
    }
    public void handleIncomingCommand(String command) throws OXOMoveException {
        int a, b, numOfCommand;
        boolean c;
        a =command.charAt(0);
        b =command.charAt(1)
        numOfCommand = command.length();
        InvalidNumber(numOfCommand);
        InvalidRowCharacter((char)a);
        InvalidColumnCharacter((char)b);
        b=b-49;
        if(a>=97 && a <=122){
            a = a - 97;
        }
        if(a>=65 && a <=90) {
            a = a - 65;
        }
        OutsideRowRange(a+1);
        OutsideColumRange(b+1);
        CellOccupied(a,b);
        c=Win(gameModel);
        if(!c) {
            gameModel.setCellOwner(a, b, gameModel.getPlayerByNumber(gameModel.getCurrentPlayerNumber()));
        }
        c=Win(gameModel);
        Drawn(gameModel);
        if(!c) {
            gameModel.setCurrentPlayerNumber(((gameModel.getCurrentPlayerNumber() + 1))%gameModel.getNumberOfPlayers());
        }
    }
    public void addRow() {
        gameModel.addRow();
    }
    public void removeRow() {
        int rows = gameModel.getNumberOfRows();
        int cols = gameModel.getNumberOfColumns();
        OXOModel tmp = new OXOModel(rows, cols, gameModel.getWinThreshold());
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                tmp.setCellOwner(i, j, gameModel.getCellOwner(i, j));
            }
        }
        tmp.removeRow();
        if((!Win(tmp)) && (!Drawn(tmp))) {
            gameModel.removeRow();
        }
    }
    public void addColumn() {
        gameModel.addColumn();
    }
    public void removeColumn() {
        int rows = gameModel.getNumberOfRows();
        int cols = gameModel.getNumberOfColumns();
        OXOModel tmp = new OXOModel(rows, cols, gameModel.getWinThreshold());
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                tmp.setCellOwner(i, j, gameModel.getCellOwner(i, j));
            }
        }
        tmp.removeColumn();
        if((!Win(tmp)) && (!Drawn(tmp))) {
            gameModel.removeColumn();
        }
    }
    public void increaseWinThreshold() {
        gameModel.setWinThreshold(gameModel.getWinThreshold()+1);
    }
    public void decreaseWinThreshold() {
        int rows = gameModel.getNumberOfRows();
        int cols = gameModel.getNumberOfColumns();
        int jud = 0;
        for(int i = 0; i< rows; i++){
            for(int j = 0; j<cols; j++ ){
                if(gameModel.getCellOwner(i,j) != null){
                    jud =1;
                }
            }
        }
        if(jud != 1){
            if(gameModel.getWinThreshold() > 3){
                gameModel.setWinThreshold(gameModel.getWinThreshold()-1);
            }
        }
    }
    public void reset() {
        for(int i = 0; i < gameModel.getNumberOfRows(); i++ ){
            for (int j = 0; j < gameModel.getNumberOfColumns(); j++ ){
                gameModel.setCellOwner(i,j,null);
            }
        }
        gameModel.setCurrentPlayerNumber(0);
    }
    public boolean Win(OXOModel gameModel){
        int jud1 = 0;
        int row = gameModel.getNumberOfRows();
        int col = gameModel.getNumberOfColumns();
        for (int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                if(gameModel.getCellOwner(i ,j) != null){
                    jud1 = 1;
                }
            }
        }
        if(jud1 == 0){
            return false;
        }
        //TODO ROW
        int jud;
        for(int rows = 0; rows < gameModel.getNumberOfRows(); rows++){
            for (int cols = 0; cols <= (gameModel.getNumberOfColumns() - gameModel.getWinThreshold()); cols++){
                jud = 0;
                if(gameModel.getCellOwner(rows, cols) != null){
                    for(int count = 1; count < gameModel.getWinThreshold(); count++ ){
                       if(gameModel.getCellOwner(rows, cols) == gameModel.getCellOwner(rows, cols+count)){
                            jud++;
                       }
                    }
                }
                if(jud == (gameModel.getWinThreshold()-1)){
                    gameModel.setWinner(gameModel.getCellOwner(rows, cols));
                   // System.out.println("row");
                    return true;

                }
            }
        }

        //TODO COLUMN
        for(int cols = 0; cols < gameModel.getNumberOfColumns(); cols++){
            for (int rows = 0; rows <= (gameModel.getNumberOfRows() - gameModel.getWinThreshold()); rows++){
                jud = 0;
                if(gameModel.getCellOwner(rows, cols) != null){for(int count = 1; count < gameModel.getWinThreshold(); count++ ){
                        if(gameModel.getCellOwner(rows, cols) == gameModel.getCellOwner(rows+count, cols)){
                            jud++;}
                    }
                }
                if(jud == (gameModel.getWinThreshold()-1)){
                    gameModel.setWinner(gameModel.getCellOwner(rows, cols));
                   // System.out.println("col");
                    return true;
                }
            }
        }
        //TODO DIAGONALS TOP LEFT
        for(int rows = 0; rows < gameModel.getNumberOfRows() - gameModel.getWinThreshold()+1; rows++){
            for(int cols = 0; cols < gameModel.getNumberOfColumns() - gameModel.getWinThreshold()+1; cols++){
                jud = 0;
                if(gameModel.getCellOwner(rows, cols) != null){
                    for(int count = 1; count < gameModel.getWinThreshold(); count++ ){
                        if(gameModel.getCellOwner(rows, cols) == gameModel.getCellOwner(rows+count, cols+count)){
                            jud ++;
                        }
                    }
                }
                if(jud == (gameModel.getWinThreshold()-1)){
                    gameModel.setWinner(gameModel.getCellOwner(rows, cols));
                   // System.out.println("top left");
                    return true;
                }
            }
        }
        //TODO DIAGONAL TOP RIGHT
        for(int rows = 0; rows < gameModel.getNumberOfRows() - gameModel.getWinThreshold()+1; rows++){
            for(int cols = gameModel.getWinThreshold()-1; cols < gameModel.getNumberOfColumns(); cols++){
                jud = 0;
                if(gameModel.getCellOwner(rows, cols) != null){
                    for(int count = 1; count < gameModel.getWinThreshold(); count++ ){
                        if(gameModel.getCellOwner(rows, cols) == gameModel.getCellOwner(rows+count, cols-count)){
                            jud ++;
                        }
                    }
                }
                if(jud == (gameModel.getWinThreshold()-1)){
                    gameModel.setWinner(gameModel.getCellOwner(rows, cols));
                   // System.out.println("top right");
                    return true;
                }
            }
        }
        return false;
    }
    public boolean Drawn(OXOModel gameModel){
        int jud = 0;
        int row = gameModel.getNumberOfRows();
        int col = gameModel.getNumberOfColumns();
        for (int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                if(gameModel.getCellOwner(i ,j) == null){
                    jud = 1;
                }
            }
        }
        if(jud == 0){
            gameModel.setGameDrawn();
            return true;
        }
        return false;
    }
    public void InvalidNumber(int numOfCommand) throws OXOMoveException.InvalidIdentifierLengthException {
        if(numOfCommand !=2){
            throw new OXOMoveException.InvalidIdentifierLengthException(numOfCommand);
        }
    }
    public void InvalidRowCharacter(char row) throws OXOMoveException.InvalidIdentifierCharacterException {
        if(!((row >= 97 && row <= 122) || (row >= 65 && row <= 90))) {
            throw new OXOMoveException.InvalidIdentifierCharacterException(OXOMoveException.RowOrColumn.ROW, row);
        }
    }
    public void InvalidColumnCharacter(char column) throws OXOMoveException.InvalidIdentifierCharacterException {
        if(!(column >= 49 && column <= 57)) {
            throw new OXOMoveException.InvalidIdentifierCharacterException(OXOMoveException.RowOrColumn.COLUMN, column);
        }
    }
    public void OutsideRowRange(int pos) throws OXOMoveException.OutsideCellRangeException {
        int row = gameModel.getNumberOfRows();
        if(pos > row){
            throw new OXOMoveException.OutsideCellRangeException(OXOMoveException.RowOrColumn.ROW, pos);
        }
    }
    public void OutsideColumRange(int pos) throws OXOMoveException.OutsideCellRangeException {
        int col = gameModel.getNumberOfColumns();
        if(pos > col){
            throw new OXOMoveException.OutsideCellRangeException(OXOMoveException.RowOrColumn.COLUMN, pos);
        }
    }
    public void CellOccupied(int row, int col) throws OXOMoveException.CellAlreadyTakenException {
        if(gameModel.getCellOwner(row, col) != null){
            throw new OXOMoveException.CellAlreadyTakenException(row, col);
        }
    }
}

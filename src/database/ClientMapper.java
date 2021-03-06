/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import businesslogic.Client;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Nik
 */
public class ClientMapper extends Mapper<Client, DatabaseManager>{

    @Override
    public Client load(int Id, DatabaseManager db) throws SQLException {
        db.startTransaction();
        String columns[] = {
            Database.Client.id,
            Database.Client.name,
            Database.Client.phone_number,
            Database.Client.type,
            Database.Client.addres
        };        
        ResultSet rs = db.executeQuery(
                QueryBilder.select(
                        Database.Client.Table,
                        columns,
                        "\"" + Database.Client.id + "\"=?",
                        new String[]{String.valueOf(Id)},
                        null,
                        Database.Client.id)
        );
        if(rs.next()){
            db.commitTransaction();        
            return new Client(rs.getInt(4), rs.getString(5).trim(), rs.getInt(1), rs.getString(2).trim(), rs.getString(3).trim());
        }else{
            db.commitTransaction();
            return null;
        }
    }

    @Override
    public ArrayList<Client> loadList(DatabaseManager db) throws SQLException {        
        db.startTransaction();
        String columns[] = {
            Database.Client.id,
            Database.Client.name,
            Database.Client.phone_number,
            Database.Client.type,
            Database.Client.addres
        };        
        ResultSet rs = db.executeQuery(
                QueryBilder.select(
                        Database.Client.Table,
                        columns,
                        null,
                        null,
                        null,
                        Database.Client.id)
        );        
        ArrayList<Client> list = new ArrayList<>();
        while(rs.next()){ 
            list.add(new Client(rs.getInt(4), rs.getString(5).trim(), rs.getInt(1), rs.getString(2).trim(), rs.getString(3).trim()));
        }
        db.commitTransaction();
        return list;
    }

    @Override
    public boolean save(Client e, DatabaseManager db) throws SQLException {
        boolean flag;
        db.startTransaction();        
        ContentValues value = new ContentValues();
        value.put(Database.Client.Table + "\".\"" + Database.Client.name,e.getName());
        value.put(Database.Client.Table + "\".\"" + Database.Client.phone_number,e.getPhoneNumber());
        value.put(Database.Client.Table + "\".\"" + Database.Client.type,String.valueOf(e.getType()));
        value.put(Database.Client.Table + "\".\"" + Database.Client.addres,e.getAddres());        
        if(e.getID()==0||e.getID()==-1){
            //insert
            int Id = generateIDs(1, db);
            e.setID(Id);
            value.put(Database.Client.id, String.valueOf(Id));
            flag = db.execute(QueryBilder.insert(Database.Client.Table, value));            
        }else{
            //update
            value.put(Database.Client.id, String.valueOf(e.getID()));
            String whereClause = "\"" + Database.Client.Table + "\".\"" + Database.Client.id +"\"=?";
            String args[] = {String.valueOf(e.getID())};
            flag = db.execute(QueryBilder.update(Database.Client.Table, value, whereClause, args));            
        }
        db.commitTransaction();
        return flag;
    }

    @Override
    public boolean saveArray(ArrayList<Client> list, DatabaseManager db) throws SQLException {
        db.startTransaction();
        boolean flag = false;
        int NewIdCount = 0;
        //цикл по всем элементам для поиска элементов без Id.
        NewIdCount = list.stream().filter((list1) -> (list1.getID() == 0 || list1.getID() == -1)).map((item) -> 1).reduce(NewIdCount, Integer::sum);

        int LastGeneratedId = (NewIdCount > 0) ? generateIDs(NewIdCount, db) : 0;
        int NextId = LastGeneratedId - NewIdCount;
        
        for (Client list1 : list) {
            ContentValues value = new ContentValues();
            value.put(Database.Client.Table + "\".\"" + Database.Client.name, list1.getName());
            value.put(Database.Client.Table + "\".\"" + Database.Client.phone_number, list1.getPhoneNumber());
            value.put(Database.Client.Table + "\".\"" + Database.Client.type, String.valueOf(list1.getType()));
            value.put(Database.Client.Table + "\".\"" + Database.Client.addres, list1.getAddres());
            if (list1.getID() == 0 || list1.getID() == -1) {
                //insert
                value.put(Database.Client.id, String.valueOf(NextId));            
                flag = db.execute(QueryBilder.insert(Database.Client.Table, value));
                list1.setID(NextId);
                NextId++;
            } else {
                //update
                String whereClause = "\"" + Database.Client.Table + "\".\"" + Database.Client.id +"\"=?";
                String[] args = {String.valueOf(list1.getID())};
                flag = db.execute(QueryBilder.update(Database.Client.Table, value, whereClause, args));            
            }
        }
        db.commitTransaction();    
        return flag;
    }
    
    @Override
    public void delete(int id,DatabaseManager db) throws SQLException{
        db.startTransaction();
        String whereClause = "\"" + Database.Client.Table + "\".\"" + Database.Client.id +"\"="+String.valueOf(id);
        db.execute(QueryBilder.delete(Database.Client.Table,whereClause));
        db.commitTransaction();
    }
    
    @Override
    public int generateIDs(int size,DatabaseManager db) throws SQLException{
        ResultSet Rset = db.executeQuery("SELECT GEN_ID( CLIENT_ID_GENERATOR, " + String.valueOf(size) + " ) FROM RDB$DATABASE;"); 
        Rset.next();        
        return Rset.getInt(1);
    }
}

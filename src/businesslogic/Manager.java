/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package businesslogic;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Nik
 */
public class Manager extends Role{
    
    public final static int ESTIMATE_SUCCESS = 0x1;
    public final static int ESTIMATE_CLIENT_NEED_PAY = 0x2;                     //клиент должен оплатить приведущие задолженности.
    public final static int ESTIMATE_ERROR_CAN_NOT_ADD = 0x3;                   //невозможно добавление возможно смета пуста
    public final static int ESTIMATE_ERROR_CAN_NOT_SET_COAST = 0x4;               //Coast < 0
    
    public final static int STORAGE_NULL = 0x5;
    public final static int WORKLIST_NULL = 0x6;
    
    String CompanyAddress;
    
    public Manager(String CompanyAddress, int Id, String Name, String PhoneNumber) {
        super(Id, Name, PhoneNumber);
        this.CompanyAddress = CompanyAddress;
    }
    
    public Order CreateOrder(Date create,int Number,ArrayList<Work> WorkList){    
        Order ord = null;
        if(WorkList != null){
            ord = new Order(create,Number);
            Estimate e = new Estimate(Estimate.MAIN, WorkList);
            ord.addEstimapte(e);
        }
        return ord;
    }
    
    public int CreateEstimate(Order ord,int type,ArrayList<Work> WorkList){
        return (WorkList != null) ? 
                CreateEstimate(ord,(new Estimate(type,WorkList))) : 
                ESTIMATE_ERROR_CAN_NOT_ADD;
    }
    
    public int CreateEstimate(Order ord,Estimate part){
        int flag = 0;
        switch(part.getType()){
            case Estimate.ADDITIONAL:
                //нужно оплатиить 85% или больше от текущей суммы заказа.
                if(ord.getCurrentCoast() <= (ord.CoastCalculation() * 0.15)){
                    if(ord.addEstimapte(part)){
                        ord.setStatus(Order.INPROGRESS);
                        flag = ESTIMATE_SUCCESS;
                    }else{
                        flag = ESTIMATE_ERROR_CAN_NOT_ADD;
                    }
                }else{
                    ord.setStatus(Order.WAITING_PAY);
                    flag = ESTIMATE_CLIENT_NEED_PAY;
                }
                break;
            case Estimate.MAIN:
                if(ord.addEstimapte(part)){
                    if(ord.setCurrentCoast(ord.CoastCalculation())){
                        ord.setStatus(Order.INPROGRESS);
                        flag = ESTIMATE_SUCCESS;
                    }else{
                    flag = ESTIMATE_ERROR_CAN_NOT_SET_COAST;
                    }
                }else{
                    flag = ESTIMATE_ERROR_CAN_NOT_ADD;
                }
                break;
        }
        return flag;
    }
    
    public ArrayList<ErrorMsg> TakeResourseFromStorage(Storage store,ArrayList<Work> WorkList,ArrayList<Resource> ProcurementList){
        ArrayList<ErrorMsg> ErrorList = new ArrayList<>();
        if(ProcurementList == null){
            ProcurementList = new ArrayList<>();
        }
        //запрос ресурсов для работ.
        if(store != null){
            if(WorkList != null){
                for (Work WorkList1 : WorkList) {
                    ArrayList<Resource> res = WorkList1.getResources();
                    for (Resource re : res) {
                        int index = store.findResoursePositionByType(re.getType());
                        if (index != -1) {
                            switch (store.TakeResources(index, re.getAmount())) {
                                case Storage.TAKE_RESORSE_SUCCESS:
                                    //успешное выполнение
                                    break;
                                case Storage.INSUFFICIENTLY_RESORSE:
                                    //данного товара недостаточно
                                    int NeedAmount = re.getAmount() - store.getResource(index).getAmount();
                                    ProcurementList.add(new Resource(NeedAmount,
                                            store.getResource(index).getType(),
                                            store.getResource(index).getCoast(),
                                            store.getResource(index).getName()));
                                    break;
                                case Storage.RESORSE_EMPTY:
                                    //на склад данного ресурса не осталось
                                    ProcurementList.add(re);
                                    break;
                                case Storage.RESORSE_NOT_FOUND:
                                    //на складе такой тип ресурс не найден
                                    ErrorList.add(new ErrorMsg(Storage.RESORSE_NOT_FOUND, re.getType()));
                                    break;
                                case Storage.STORAGE_EMPTY:
                                    //проблема с инициализацией склада
                                    ErrorList.add(new ErrorMsg(Storage.STORAGE_EMPTY, -1));
                                    break;
                            }
                        }else{
                            ErrorList.add(new ErrorMsg(Storage.RESORSE_NOT_FOUND, -1));
                        }
                    }
                }
                if(!ProcurementList.isEmpty()){
                    int flag = SendResourseToStorage(store,ProcurementList);
                    if(flag != Storage.SEND_RESORSE_SUCCESS){
                        ErrorList.add(new ErrorMsg(flag, -1));
                    }
                }
            }else{
                ErrorList.add(new ErrorMsg(WORKLIST_NULL, -1));
            }
        }else{
            ErrorList.add(new ErrorMsg(STORAGE_NULL, -1));
        }
        return ErrorList;
    }
    
    /*
     * @return - возвращает успех или тип ошибки. 
     */
    public int SendResourseToStorage(Storage store,ArrayList<Resource> ResList){
        //отправка ресурсов на склад.
        int Flag;
        for (Resource ResList1 : ResList) {
            Flag = store.SendResources(ResList1.getType(), ResList1.getAmount());
            if (Flag != Storage.SEND_RESORSE_SUCCESS) {
                return Flag;
            }
        }
        return Storage.SEND_RESORSE_SUCCESS;
    }
    
    public boolean CloseOrder(boolean ClientAceptWork,Order ord,Date End){
        if(ord.getStatus()== Order.WAITING_ACKNOWLEDGMENT_PAY){
            if(ord.getCurrentCoast() == 0){
                ord.setStatus(Order.CLOSE);
                return ord.CloseEstimate(End);
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    
    public void setCompanyAddress(String CompanyAddress) {
        this.CompanyAddress = CompanyAddress;
    }

    public String getCompanyAddress() {
        return CompanyAddress;
    }
    
}

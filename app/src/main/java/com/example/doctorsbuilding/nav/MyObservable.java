package com.example.doctorsbuilding.nav;

import android.app.TaskStackBuilder;
import android.graphics.Bitmap;
import android.graphics.Interpolator;

import com.example.doctorsbuilding.nav.Databases.DatabaseAdapter;
import com.example.doctorsbuilding.nav.Dr.Clinic.Office;
import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by hossein on 1/25/2017.
 */


public class MyObservable {

    public static DatabaseAdapter database = new DatabaseAdapter(G.context);

    public static Observable<User> getUserInfoWithPic(final String username, final String password) {

        return Observable.create(new ObservableOnSubscribe<User>() {
            @Override
            public void subscribe(ObservableEmitter<User> emitter) throws MyException {
                try {
                    User user = WebService.invokeGetUserInfoWithPicWS(username, password);
                    emitter.onNext(user);
                    emitter.onComplete();

                } catch (Exception ex) {
                    if (!emitter.isDisposed())
                        emitter.onError(ex);
                }
            }
        });
    }

    public static Observable<Integer> getRoleInAll(final String username, final String password) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws MyException {
                try {
                    int role = WebService.invokeGetRoleInAllWS(username, password);
                    emitter.onNext(role);
                    emitter.onComplete();
                } catch (Exception ex) {
                    if (!emitter.isDisposed())
                        emitter.onError(ex);
                }

            }
        });
    }

    public static Observable<ArrayList<Office>> getOfficeInfoFromPhone() {
        return Observable.create(new ObservableOnSubscribe<ArrayList<Office>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<Office>> emitter) throws MyException {
                try {
                    ArrayList<Office> offices = new ArrayList<Office>();
                    if (database.openConnection()) {
                        offices = database.getOfficeInfo();
                    }
                    emitter.onNext(offices);
                    emitter.onComplete();
                } catch (Exception ex) {
                    if (!emitter.isDisposed())
                        emitter.onError(ex);
                }
            }
        });
    }

    public static Observable<ArrayList<Office>> getOfficeInfoFromWeb(final String username, final String password) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<Office>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<Office>> emitter) throws MyException {
                try {
                    ArrayList<Office> offices = WebService.invokeGetOfficeForAllWS(username, password);
                    emitter.onNext(offices);
                    emitter.onComplete();
                } catch (Exception ex) {
                    if (!emitter.isDisposed())
                        emitter.onError(ex);
                }
            }
        });
    }

    public static Observable<Office> getOfficeInfoFromWebWithOfficeId(final String username, final String password, final int officeId) {
        return Observable.create(new ObservableOnSubscribe<Office>() {
            @Override
            public void subscribe(ObservableEmitter<Office> emitter) throws MyException {
                try {
                    Office offices = WebService.invokeGetOfficeInfoWS(username, password, officeId);
                    emitter.onNext(offices);
                    emitter.onComplete();
                } catch (Exception ex) {
                    if (!emitter.isDisposed())
                        emitter.onError(ex);
                }
            }
        });
    }

    public static Observable<String> addOfficeForUser(final String username, final String password, final int officeId) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws MyException {
                try {
                    String result = WebService.invokeAddOfficeForUserWS(username, password, officeId);
                    emitter.onNext(result);
                    emitter.onComplete();
                } catch (Exception ex) {
                    if (!emitter.isDisposed())
                        emitter.onError(ex);
                }
            }
        });
    }

    public static Observable<Bitmap> getDoctorPic(final String username, final String password, final int officeId) {
        return Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> emitter) throws MyException {
                try {
                    Bitmap pic = WebService.invokeGetDoctorPicWS(username, password, officeId);
                    emitter.onNext(pic);
                    emitter.onComplete();
                } catch (Exception ex) {
                    if (!emitter.isDisposed())
                        emitter.onError(ex);
                }
            }
        });
    }

    public static Observable<PhotoDesc> getGalleryPicFromPhone(final int picId) {
        return Observable.create(new ObservableOnSubscribe<PhotoDesc>() {
            @Override
            public void subscribe(ObservableEmitter<PhotoDesc> emitter) throws MyException {
                try {
                    if (database.openConnection()) {
                        PhotoDesc photo = database.getImageFromGallery(picId);
                        emitter.onNext(photo);
                        emitter.onComplete();
                    }
                } catch (Exception ex) {
                    if (!emitter.isDisposed())
                        emitter.onError(ex);
                }
            }
        });
    }

    public static Observable<PhotoDesc> getGalleryPicFromWeb(final String username, final String password, final int officeId, final int picId) {
        return Observable.create(new ObservableOnSubscribe<PhotoDesc>() {
            @Override
            public void subscribe(ObservableEmitter<PhotoDesc> emitter) throws MyException {
                try {
                    PhotoDesc photo = WebService.invokeGetGalleryPicWS(username, password, officeId, picId);
                    emitter.onNext(photo);
                    emitter.onComplete();
                } catch (Exception ex) {
                    if (!emitter.isDisposed())
                        emitter.onError(ex);
                }
            }
        });
    }

    public static Observable<ArrayList<Integer>> getGalleryPicIds(final String username, final String password, final int officeId) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<Integer>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<Integer>> emitter) throws MyException {
                try {
                    ArrayList<Integer> ids = WebService.invokegetAllGalleryPicIdWS(username, password, officeId);
                    emitter.onNext(ids);
                    emitter.onComplete();
                } catch (Exception ex) {
                    if (!emitter.isDisposed())
                        emitter.onError(ex);
                }
            }
        });
    }

    public static Observable<Integer> getRoleInOffice(final String username, final String password, final int officeId) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws MyException {
                try {
                    int role = WebService.invokeGetRoleInOfficeWS(username, password, officeId);
                    emitter.onNext(role);
                    emitter.onComplete();
                } catch (Exception ex) {
                    if (!emitter.isDisposed())
                        emitter.onError(ex);
                }
            }
        });
    }

    public static Observable<ArrayList<MessageInfo>> getAllUnreadMessage(final String username, final String password) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<MessageInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<MessageInfo>> emitter) throws MyException {
                try {
                    ArrayList<MessageInfo> messageInfos = WebService.invokeGetAllUnreadMessagesWS(username, password);
                    emitter.onNext(messageInfos);
                    emitter.onComplete();
                } catch (Exception ex) {
                    if (!emitter.isDisposed())
                        emitter.onError(ex);
                }
            }
        });
    }

    public static Observable<String> deleteOfficeForUser(final String username, final String password, final int officeId) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws MyException {
                try {
                    String result = WebService.invokeDeleteOfficeForUserWS(username, password, officeId);
                    emitter.onNext(result);
                    emitter.onComplete();
                } catch (Exception ex) {
                    if (!emitter.isDisposed())
                        emitter.onError(ex);
                }
            }
        });
    }

    public static Observable<ArrayList<User>> searchUser(final String username, final String password, final String lastname) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<User>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<User>> emitter) throws MyException {
                try {
                    ArrayList<User> users = WebService.invokeSearchUserWS(username, password, lastname);
                    emitter.onNext(users);
                    emitter.onComplete();
                } catch (Exception ex) {
                    if (!emitter.isDisposed())
                        emitter.onError(ex);
                }
            }
        });
    }

    public static Observable<ArrayList<TaskGroup>> getTaskGroups(final String username, final String password, final int officeId, final int turnId) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<TaskGroup>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<TaskGroup>> emitter) throws MyException {
                try {
                    ArrayList<TaskGroup> taskGroups = WebService.invokeGetTaskGroupsWS(username, password, officeId, turnId);
                    emitter.onNext(taskGroups);
                    emitter.onComplete();
                } catch (Exception ex) {
                    if (!emitter.isDisposed())
                        emitter.onError(ex);
                }
            }
        });
    }

    public static Observable<ArrayList<Task>> getTasks(final String username, final String password, final int officeId, final int taskGroupId) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<Task>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<Task>> emitter) throws MyException {
                try {
                    ArrayList<Task> tasks = WebService.invokeGetTaskWS(username, password, officeId, taskGroupId);
                    emitter.onNext(tasks);
                    emitter.onComplete();
                } catch (Exception ex) {
                    if (!emitter.isDisposed())
                        emitter.onError(ex);
                }

            }
        });
    }

    public static Observable<ArrayList<Assistant>> getAssistantForTurn(final String username, final String password, final int officeId, final int turnId, final int taskGroupId) {
        return Observable.create(new ObservableOnSubscribe<ArrayList<Assistant>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<Assistant>> emitter) throws MyException {
                try {
                    ArrayList<Assistant> assistants = WebService.getAssistantOfficeTaskWS(username, password, officeId, turnId, taskGroupId);
                    emitter.onNext(assistants);
                    emitter.onComplete();
                } catch (Exception ex) {
                    if (!emitter.isDisposed())
                        emitter.onError(ex);
                }

            }
        });
    }

    public static Observable<Integer> reserveTurnForUser(final String username, final String password, final Reservation reservation) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws MyException {
                try {
                    int reservationId = WebService.invokeResevereForUser(username, password, reservation);
                    emitter.onNext(reservationId);
                    emitter.onComplete();
                } catch (Exception ex) {
                    if (!emitter.isDisposed())
                        emitter.onError(ex);
                }
            }
        });
    }

    public static Observable<Integer> reserveTurnForMe(final String username, final String password, final Reservation reservation, int resNum) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws MyException {
                try {
                    int reservationId = WebService.invokeReserveForMeWS(G.UserInfo.getUserName(), G.UserInfo.getPassword(), reservation, G.resNum);
                    emitter.onNext(reservationId);
                    emitter.onComplete();
                } catch (Exception ex) {
                    if (!emitter.isDisposed())
                        emitter.onError(ex);
                }
            }
        });
    }

    public static Observable<Integer> reserveTurnForGuest(final String username, final String password, final Reservation reservation, final int cityId) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws MyException {
                try {
                    int reservationId = WebService.invokeReserveForGuestWS(username, password, reservation, cityId);
                    emitter.onNext(reservationId);
                    emitter.onComplete();
                } catch (Exception ex) {
                    if (!emitter.isDisposed())
                        emitter.onError(ex);
                }
            }
        });
    }

    public static Observable<Integer> reserveTurnForGuestFromUser(final String username, final String password, final Reservation reservation, final int cityId) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws MyException {
                try {
                    int reservationId = WebService.invokeReserveForGuestFromUserWS(username, password, reservation, G.resNum);
                    emitter.onNext(reservationId);
                    emitter.onComplete();
                } catch (Exception ex) {
                    if (!emitter.isDisposed())
                        emitter.onError(ex);
                }
            }
        });
    }
}

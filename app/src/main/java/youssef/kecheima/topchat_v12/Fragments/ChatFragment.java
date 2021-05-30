package youssef.kecheima.topchat_v12.Fragments;



import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.ArrayList;
import java.util.List;
import youssef.kecheima.topchat_v12.Adapters.ChatListAdapter;
import youssef.kecheima.topchat_v12.Model.Chat;
import youssef.kecheima.topchat_v12.Model.ChatList;
import youssef.kecheima.topchat_v12.Model.User;
import youssef.kecheima.topchat_v12.R;

public class ChatFragment extends Fragment {

    public ChatFragment() {
        // Required empty public constructor
    }

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private List<User> userList;
    private List<ChatList> chatLists;
    private ChatListAdapter chatListAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        //Cast Components
        recyclerView = view.findViewById(R.id.RecyclerChatContact);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //FireBase Instances
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        chatLists=new ArrayList<>();

        FirebaseMessaging.getInstance().subscribeToTopic(firebaseUser.getUid());


        databaseReference = FirebaseDatabase.getInstance().getReference("Inbox").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatLists.clear();
                for(DataSnapshot data : snapshot.getChildren()){
                    ChatList chatList=data.getValue(ChatList.class);
                    chatLists.add(chatList);
                }
                loadChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }


    private void loadChats() {
        userList = new ArrayList<>();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot data:queryDocumentSnapshots){
                    User user=data.toObject(User.class);
                    for(ChatList list:chatLists){
                       if(user.getId()!=null && user.getId().equals(list.getChat_id())){
                            userList.add(user);
                            break;
                        }
                    }
                    chatListAdapter= new ChatListAdapter(userList,getContext());
                    recyclerView.setAdapter(chatListAdapter);
                    for(int i=0;i<userList.size();i++){
                        lastMessage(userList.get(i).getId());
                    }
                }
            }
        });
    }

    private void lastMessage(String userId) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Lastmessage="default";
                for(DataSnapshot data:snapshot.getChildren()){
                    Chat chat=data.getValue(Chat.class);
                    if(chat==null){
                        continue;
                    }
                    String sender=chat.getSender();
                    String receiver=chat.getReceiver();
                    if(sender==null || receiver==null){
                        continue;
                    }
                    if(receiver.equals(firebaseUser.getUid()) && sender.equals(userId) ||
                        receiver.equals(userId) && sender.equals(firebaseUser.getUid()))
                    {
                        Lastmessage=chat.getMessage();
                    }
                }
                chatListAdapter.setLastMessageMap(userId,Lastmessage);
                chatListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
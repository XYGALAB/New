package id.kharisma.studio.vircle.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import id.kharisma.studio.vircle.Model.Community
import id.kharisma.studio.vircle.R

class CommunityAdapter(private var mContext: Context,
private var mCommunity: List<Community>) : RecyclerView.Adapter<CommunityAdapter.ViewHolder>()
{
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_community,parent,false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return mCommunity.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val community = mCommunity[position]
        holder.comNameTextView.text = community.getCommunityName()
        Picasso.get().load(community.getcommunityImage()).placeholder(R.drawable.profile).into(holder.comImage)
        firebaseUser?.let { holder.bindItems(community, it) }
    }
    class ViewHolder ( @NonNull itemView: View) : RecyclerView.ViewHolder(itemView){
        var comNameTextView: TextView = itemView.findViewById(R.id.communityname)
        var comJmlThreadTextView: TextView = itemView.findViewById(R.id.jml_thread)
        lateinit var comJmlMemberTextView: TextView
        var comImage: CircleImageView = itemView.findViewById(R.id.community_item)
        lateinit var joinButton: Button
        @SuppressLint("SuspiciousIndentation")
        fun bindItems(community: Community,firebaseUser: FirebaseUser){
            joinButton = itemView.findViewById(R.id.btnJoin)
            comJmlMemberTextView = itemView.findViewById(R.id.jml_member)
            mJoin(comJmlMemberTextView,community.getcommunityId())
            joinButton.setOnClickListener{
                if(joinButton.text.toString() == "Join"){
                    firebaseUser?.uid.let{it1 ->
                        FirebaseDatabase.getInstance("https://vircle-77b59-default-rtdb.firebaseio.com/").reference
                            .child("Community").child(community.getcommunityId())
                            .child("Member").child(it1.toString())
                            .setValue(true)
                    }
                }else{
                    firebaseUser?.uid.let{it1 ->
                        FirebaseDatabase.getInstance("https://vircle-77b59-default-rtdb.firebaseio.com/").reference
                            .child("Community").child(community.getcommunityId())
                            .child("Member").child(it1.toString())
                            .removeValue()
                    }
                }
            }

        }

        private fun mJoin(comJmlMemberTextView: TextView, getcommunityId: String) {
            val ref = FirebaseDatabase.getInstance().reference.child("Community").child(getcommunityId)
                .child("Member")
            ref.addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) =
                    if (snapshot.childrenCount > 0){
                        comJmlMemberTextView.text = snapshot.childrenCount.toString()
                        comJmlMemberTextView.visibility = View.VISIBLE
                    }else{
                        comJmlMemberTextView.visibility = View.GONE
                    }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }


    }
}
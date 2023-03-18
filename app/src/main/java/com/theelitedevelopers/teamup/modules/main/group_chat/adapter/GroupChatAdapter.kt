package com.theelitedevelopers.teamup.modules.main.group_chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.theelitedevelopers.teamup.R
import com.theelitedevelopers.teamup.core.data.local.SharedPref
import com.theelitedevelopers.teamup.core.utils.AppUtils
import com.theelitedevelopers.teamup.core.utils.AppUtils.Companion.fromTimeStampToString
import com.theelitedevelopers.teamup.core.utils.Constants
import com.theelitedevelopers.teamup.databinding.ChatInLayoutBinding
import com.theelitedevelopers.teamup.databinding.ChatOutLayoutBinding
import com.theelitedevelopers.teamup.databinding.GroupChatInLayoutBinding
import com.theelitedevelopers.teamup.databinding.GroupChatOutLayoutBinding
import com.theelitedevelopers.teamup.modules.data.models.Chat
import java.text.ParseException

class GroupChatAdapter(var context : Context, var messageList : ArrayList<Chat>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val RECEIVE = 0
    val SEND = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == 0){
            //inflate the Received Layout
            val binding : GroupChatInLayoutBinding = GroupChatInLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            return ReceivedViewHolder(binding)
        }else {
            //inflate the Send Layout
            val binding : GroupChatOutLayoutBinding = GroupChatOutLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            return SentViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder.javaClass == SentViewHolder::class.java){
            val viewHolder = holder as SentViewHolder

            //set the message
            holder.binding.inboxOutMessage.text = messageList[position].message

            holder.binding.inboxOutDate.text = AppUtils.getSingleInboxDate(
                fromTimeStampToString(messageList[position].date!!.seconds)
            )

            /* Here, we want to check the next date
             * after this position. If it is the same
             * with this, we'll make this date GONE
             */if (position >= 0) {
                //initialize next position
                val nextPosition = position + 1
                if (nextPosition < messageList.size) {
                    if (messageList[position].uid
                            .equals(SharedPref.getInstance(context).getString(Constants.UID))
                            && messageList[nextPosition].uid
                            .equals(SharedPref.getInstance(context).getString(Constants.UID))
                    ) {
                        try {
                            if (AppUtils.convertToDateWithoutSeconds(
                                    fromTimeStampToString(
                                        messageList[position].date.seconds
                                    )
                                )?.equals(
                                    AppUtils.convertToDateWithoutSeconds(
                                        fromTimeStampToString(
                                            messageList[nextPosition].date.seconds
                                        )
                                    )
                                ) == true
                            ) {
                                holder.binding.inboxOutDate.visibility = View.GONE
                            } else {
                                holder.binding.inboxOutDate.visibility = View.VISIBLE
                            }
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }
                    } else {
                        holder.binding.inboxOutDate.visibility = View.VISIBLE
                    }
                }
            }

        }else {
            val viewHolder = holder as ReceivedViewHolder
            Picasso.get()
                .load(messageList[position].image)
                .placeholder(R.drawable.academia_profile)
                .into(holder.binding.inboxImage)

            //set the message
            holder.binding.inboxInMessage.text = messageList[position].message
            holder.binding.inboxName.text = messageList[position].name

            holder.binding.inboxInDate.text = AppUtils.getSingleInboxDate(
                fromTimeStampToString(messageList[position].date!!.seconds)
            )


/*            *//* Here, we want to check the next date
             * after this position. If it is the same
             * with this, we'll make this date GONE
             *//*if (position >= 0) {
                //initialize next position
                val nextPosition = position + 1
                if (nextPosition < messageList.size) {
                    if (!messageList[position].uid
                            .equals(SharedPref.getInstance(context).getString(Constants.UID)) &&
                        !messageList[nextPosition].uid
                            .equals(SharedPref.getInstance(context).getString(Constants.UID))
                    ) {
                        try {
                            if (AppUtils.convertToDateWithoutSeconds(
                                    fromTimeStampToString(
                                        messageList[position].date.seconds
                                    )
                                )?.equals(
                                    AppUtils.convertToDateWithoutSeconds(
                                        fromTimeStampToString(
                                            messageList[nextPosition].date.seconds
                                        )
                                    )
                                ) == true
                            ) {
                                holder.binding.inboxInDate.visibility = View.GONE
                            } else {
                                holder.binding.inboxInDate.visibility = View.VISIBLE
                            }
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }
                    } else {
                        holder.binding.inboxInDate.visibility = View.VISIBLE
                    }
                }
            }*/

        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(SharedPref.getInstance(context).getString(Constants.UID) == messageList[position].uid){
            SEND
        }else {
            RECEIVE
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    fun setList(messageList: ArrayList<Chat>){
        this.messageList = messageList;
        notifyDataSetChanged()
    }

    class SentViewHolder(var binding : GroupChatOutLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    class ReceivedViewHolder(var binding : GroupChatInLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}
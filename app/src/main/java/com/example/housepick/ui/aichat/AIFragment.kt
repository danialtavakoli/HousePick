package com.example.housepick.ui.aichat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.housepick.R
import com.example.housepick.classes.Message
import com.example.housepick.databinding.FragmentAiChatBinding

class AIFragment : Fragment() {

    private lateinit var binding: FragmentAiChatBinding
    private lateinit var viewModel: AIViewModel
    private lateinit var adapter: MessagesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(AIViewModel::class.java)
        binding = FragmentAiChatBinding.inflate(inflater, container, false)

        // Initialize RecyclerView
        adapter = MessagesAdapter(viewModel.messages)
        binding.rvMessages.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMessages.adapter = adapter

        // Handle Send Button
        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString()
            if (message.isNotEmpty()) {
                viewModel.sendMessageToAI(message)
                binding.etMessage.text.clear() // Clear input field
            }
        }

        // Observe message updates and update RecyclerView
        viewModel.messageLiveData.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged() // Update RecyclerView when new messages are added
            binding.rvMessages.scrollToPosition(viewModel.messages.size - 1) // Scroll to the latest message
        }

        return binding.root
    }
}

class MessagesAdapter(private val messages: List<Message>) :
    RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.tvMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layout = if (viewType == 1) {
            R.layout.item_user_message // Layout for user message
        } else {
            R.layout.item_ai_message // Layout for AI message
        }

        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.messageText.text = message.text
    }

    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) 1 else 0
    }
}

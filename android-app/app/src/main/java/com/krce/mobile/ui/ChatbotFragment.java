package com.krce.mobile.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.krce.mobile.R;
import com.krce.mobile.api.ApiClient;
import com.krce.mobile.model.ChatRequest;
import com.krce.mobile.model.ChatResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotFragment extends Fragment {
    private LinearLayout messages;
    private EditText input;
    private Button send;
    private ProgressBar progress;

    public ChatbotFragment() { super(R.layout.fragment_chatbot); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        messages = view.findViewById(R.id.chatMessages);
        input = view.findViewById(R.id.chatInput);
        send = view.findViewById(R.id.chatSend);
        progress = view.findViewById(R.id.chatProgress);

        addBot("Hi! I'm KRCE Bot. I can search the official KRCE website and return the source page for my answer.");

        view.findViewById(R.id.quickAdmissions).setOnClickListener(v -> ask("What are the admissions and programmes offered at KRCE?"));
        view.findViewById(R.id.quickDepartments).setOnClickListener(v -> ask("What departments are available at KRCE?"));
        view.findViewById(R.id.quickFacilities).setOnClickListener(v -> ask("What facilities are available at KRCE?"));
        view.findViewById(R.id.quickLibrary).setOnClickListener(v -> ask("Tell me about the KRCE library."));
        send.setOnClickListener(v -> ask(input.getText().toString().trim()));
    }

    private void ask(String question) {
        if (question == null || question.isBlank()) return;
        addUser(question);
        input.setText("");
        send.setEnabled(false);
        progress.setVisibility(View.VISIBLE);

        ApiClient.service().ask(new ChatRequest(question, true)).enqueue(new Callback<ChatResponse>() {
            @Override public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                send.setEnabled(true);
                progress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) addBotResponse(response.body());
                else addBot("I couldn't complete the live search. Please make sure the Spring Boot backend is running.");
            }
            @Override public void onFailure(Call<ChatResponse> call, Throwable t) {
                send.setEnabled(true);
                progress.setVisibility(View.GONE);
                addBot("The backend is not reachable. Start Spring Boot and try again.");
            }
        });
    }

    private void addUser(String text) {
        TextView bubble = makeBubble(text, true);
        messages.addView(bubble);
    }

    private void addBot(String text) {
        TextView bubble = makeBubble(text, false);
        messages.addView(bubble);
    }

    private void addBotResponse(ChatResponse response) {
        TextView bubble = makeBubble(response.answer, false);
        messages.addView(bubble);

        if (response.sourceUrl != null && !response.sourceUrl.isBlank()) {
            Button source = new Button(requireContext());
            source.setText("Open official source ↗");
            source.setAllCaps(false);
            source.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(response.sourceUrl))));
            messages.addView(source);
        }
    }

    private TextView makeBubble(String text, boolean user) {
        TextView bubble = new TextView(requireContext());
        bubble.setText(text);
        bubble.setTextSize(16);
        bubble.setTextColor(getResources().getColor(user ? R.color.white : R.color.black));
        bubble.setBackgroundResource(user ? R.drawable.bg_user_bubble : R.drawable.bg_bot_bubble);
        bubble.setPadding(20, 14, 20, 14);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(user ? 60 : 0, 6, user ? 0 : 60, 6);
        bubble.setLayoutParams(lp);
        return bubble;
    }
}

package com.krce.mobile.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.krce.mobile.R;
import com.krce.mobile.api.ApiClient;
import com.krce.mobile.api.KrceApi;
import com.krce.mobile.model.ContactRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactFragment extends Fragment {
    public ContactFragment() { super(R.layout.fragment_contact); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText name = view.findViewById(R.id.contactName);
        EditText phone = view.findViewById(R.id.contactPhone);
        EditText message = view.findViewById(R.id.contactMessage);
        Button submit = view.findViewById(R.id.contactSubmit);

        submit.setOnClickListener(v -> {
            if (name.getText().toString().trim().isBlank() || phone.getText().toString().trim().isBlank()) {
                Toast.makeText(requireContext(), "Name and phone number are required", Toast.LENGTH_SHORT).show();
                return;
            }
            submit.setEnabled(false);
            ApiClient.service().contact(new ContactRequest(
                    name.getText().toString().trim(),
                    phone.getText().toString().trim(),
                    message.getText().toString().trim()
            )).enqueue(new Callback<KrceApi.SimpleResponse>() {
                @Override public void onResponse(Call<KrceApi.SimpleResponse> call, Response<KrceApi.SimpleResponse> response) {
                    submit.setEnabled(true);
                    Toast.makeText(requireContext(), response.isSuccessful() ? "Message submitted" : "Submission failed", Toast.LENGTH_SHORT).show();
                    if (response.isSuccessful()) message.setText("");
                }
                @Override public void onFailure(Call<KrceApi.SimpleResponse> call, Throwable t) {
                    submit.setEnabled(true);
                    Toast.makeText(requireContext(), "Backend unavailable", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}

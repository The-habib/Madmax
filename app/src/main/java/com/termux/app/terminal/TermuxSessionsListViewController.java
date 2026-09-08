package com.termux.app.terminal;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.card.MaterialCardView;
import com.termux.R;
import com.termux.app.TermuxActivity;
import com.termux.shared.termux.shell.command.runner.terminal.TermuxSession;
import com.termux.terminal.TerminalSession;

import java.util.List;

/**
 * Modern Material 3 controller for terminal session items in the navigation drawer.
 */
public class TermuxSessionsListViewController extends ArrayAdapter<TermuxSession> implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    final TermuxActivity mActivity;

    public TermuxSessionsListViewController(TermuxActivity activity, List<TermuxSession> sessionList) {
        super(activity.getApplicationContext(), R.layout.item_terminal_sessions_list, sessionList);
        this.mActivity = activity;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View sessionRowView = convertView;
        if (sessionRowView == null) {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            sessionRowView = inflater.inflate(R.layout.item_terminal_sessions_list, parent, false);
        }

        MaterialCardView card = sessionRowView.findViewById(R.id.session_card);
        View activeIndicator = sessionRowView.findViewById(R.id.session_active_indicator);
        FrameLayout iconBadge = sessionRowView.findViewById(R.id.session_icon_badge);
        ImageView sessionIcon = sessionRowView.findViewById(R.id.session_icon);
        TextView titleView = sessionRowView.findViewById(R.id.session_title);
        TextView subtitleView = sessionRowView.findViewById(R.id.session_subtitle);
        TextView statusPill = sessionRowView.findViewById(R.id.session_status_pill);
        ImageButton closeButton = sessionRowView.findViewById(R.id.session_close_button);

        TermuxSession item = getItem(position);
        if (item == null || item.getTerminalSession() == null) {
            if (titleView != null) titleView.setText("null session");
            return sessionRowView;
        }

        final TerminalSession sessionAtRow = item.getTerminalSession();
        final TerminalSession currentSession = mActivity.getCurrentSession();
        final boolean isActive = (sessionAtRow == currentSession);

        // Active State Styling
        if (activeIndicator != null) {
            activeIndicator.setVisibility(isActive ? View.VISIBLE : View.INVISIBLE);
        }

        if (card != null) {
            if (isActive) {
                card.setStrokeColor(0x80FF3B30);
                card.setCardBackgroundColor(0xFF241616);
            } else {
                card.setStrokeColor(0xFF282830);
                card.setCardBackgroundColor(0xFF17171C);
            }
        }

        if (iconBadge != null) {
            iconBadge.setBackgroundResource(isActive ? R.drawable.bg_icon_badge_primary : R.drawable.bg_session_icon_container);
        }

        if (sessionIcon != null) {
            sessionIcon.setColorFilter(isActive ? 0xFFFF3B30 : 0xFFD0D0D4);
        }

        // Title and Subtitle Binding
        String name = sessionAtRow.mSessionName;
        String displayIndex = String.valueOf(position + 1);
        String sessionNamePart = TextUtils.isEmpty(name) ? "Session " + displayIndex : name;

        if (titleView != null) {
            titleView.setText(displayIndex + ". " + sessionNamePart);
            titleView.setTextColor(isActive ? 0xFFFFFFFF : 0xFFF2F2F5);
        }

        String sessionTitle = sessionAtRow.getTitle();
        String cwd = sessionAtRow.getCwd();
        String subtitleText = !TextUtils.isEmpty(sessionTitle) ? sessionTitle : (!TextUtils.isEmpty(cwd) ? cwd : "bash");

        if (subtitleView != null) {
            subtitleView.setText(subtitleText);
        }

        // Status Pill Binding
        boolean isRunning = sessionAtRow.isRunning();
        if (statusPill != null) {
            if (isRunning) {
                statusPill.setText("RUNNING");
                statusPill.setTextColor(0xFF34C759);
                statusPill.setBackgroundResource(R.drawable.bg_session_running_pill);
            } else {
                int exitStatus = sessionAtRow.getExitStatus();
                statusPill.setText(exitStatus == 0 ? "DONE" : "EXIT " + exitStatus);
                statusPill.setTextColor(0xFF8E8E93);
                statusPill.setBackgroundResource(R.drawable.bg_session_exited_pill);
            }
        }

        if (titleView != null) {
            if (isRunning) {
                titleView.setPaintFlags(titleView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                titleView.setPaintFlags(titleView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        // Click actions directly on card
        if (card != null) {
            card.setOnClickListener(v -> {
                mActivity.getTermuxTerminalSessionClient().setCurrentSession(sessionAtRow);
                mActivity.getDrawer().closeDrawers();
            });

            card.setOnLongClickListener(v -> {
                mActivity.getTermuxTerminalSessionClient().renameSession(sessionAtRow);
                return true;
            });
        }

        // Close / Terminate Button
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> {
                if (sessionAtRow.isRunning()) {
                    sessionAtRow.finishIfRunning();
                }
                mActivity.getTermuxTerminalSessionClient().removeFinishedSession(sessionAtRow);
            });
        }

        return sessionRowView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TermuxSession clickedSession = getItem(position);
        if (clickedSession != null) {
            mActivity.getTermuxTerminalSessionClient().setCurrentSession(clickedSession.getTerminalSession());
            mActivity.getDrawer().closeDrawers();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final TermuxSession selectedSession = getItem(position);
        if (selectedSession != null) {
            mActivity.getTermuxTerminalSessionClient().renameSession(selectedSession.getTerminalSession());
            return true;
        }
        return false;
    }
}

package cc.joyreactor.views;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

public class LocalDateTimeSpinner extends JPanel {

    private LocalDateTime dt = LocalDateTime.now();

    private JLabel dayLabel;
    private JLabel monthLabel;
    private JLabel yearLabel;

    public LocalDateTimeSpinner() {
        super(new FlowLayout(FlowLayout.LEFT));
        initUI();
    }

    private void initUI() {
        dayLabel = new JLabel();
        setStyle(dayLabel);
        add(dayLabel);

        monthLabel = new JLabel();
        setStyle(monthLabel);
        add(monthLabel);

        yearLabel = new JLabel();
        setStyle(yearLabel);
        add(yearLabel);


    }

    private void setStyle(JLabel label) {
        label.setFont(label.getFont().deriveFont(Font.ITALIC, 16.0f));
        label.setBorder(UIManager.getBorder("ScrollPane.border"));
    }

    private void update() {
        dayLabel.setText(" " + dt.getDayOfMonth() + " ");
        monthLabel.setText(" " + dt.getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " ");
        yearLabel.setText(" " + dt.getYear() + " ");
    }

    public LocalDateTime getDt() {
        return LocalDateTime.of(dt.toLocalDate(), dt.toLocalTime());
    }

    public void setDt(LocalDateTime dt) {
        this.dt = LocalDateTime.of(dt.toLocalDate(), dt.toLocalTime());
        update();
    }
}

package esppd;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class TestPengajuanSppd extends ReuseLoginSso {

    private WebDriverWait getWait(int seconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(seconds));
    }

    public void clickButtonByText(String buttonText) {
        WebDriverWait wait = getWait(5);
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[text()='" + buttonText + "']]")));
        try {
            button.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
        }
    }

    public void selectDynamicDropdownByString(String inputId, String optionText) {
        WebDriverWait wait = getWait(10);

        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@class='ant-modal-body']//input[@id='" + inputId + "']")));

        WebElement container = input.findElement(By.xpath("./ancestor::div[contains(@class,'ant-select-selector')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", container);
        container.click();

        input.sendKeys(optionText);

        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'ant-select-dropdown')]//div[@class='ant-select-item-option-content' and normalize-space(text())='" + optionText + "']")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);

        // Validasi terpilih
        WebElement selected = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@class='ant-modal-body']//span[contains(@class,'ant-select-selection-item') and normalize-space(text())='" + optionText + "']")));
        Assert.assertEquals(selected.getText().trim(), optionText);
    }

    public void setTanggalRange(String labelAwal, String dateAwal, String labelAkhir, String dateAkhir) {
        WebDriverWait wait = getWait(10);

        WebElement inputAwal = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'modal-form-label') and contains(.,'" + labelAwal + "')]/following::input[1]")));
        inputAwal.click();

        WebElement popupAwal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".ant-picker-dropdown:not(.ant-picker-dropdown-hidden)")));
        pilihHariDalamPopup(popupAwal, dateAwal);

        wait.until(ExpectedConditions.attributeToBe(inputAwal, "value", formatTanggal(dateAwal)));
        wait.until(ExpectedConditions.invisibilityOf(popupAwal));

        WebElement inputAkhir = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'modal-form-label') and contains(.,'" + labelAkhir + "')]/following::input[1]")));
        inputAkhir.click();

        WebElement popupAkhir = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".ant-picker-dropdown:not(.ant-picker-dropdown-hidden)")));
        pilihHariDalamPopup(popupAkhir, dateAkhir);

        wait.until(ExpectedConditions.attributeToBe(inputAkhir, "value", formatTanggal(dateAkhir)));

        WebElement btnNext = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//button[span[text()='Selanjutnya'] and contains(@class,'ant-btn-primary')]")));
        Assert.assertTrue(btnNext.isDisplayed(), "Button 'Selanjutnya' seharusnya aktif setelah kedua tanggal dipilih");
    }

    public void pilihHariDalamPopup(WebElement popup, String dateValue) {
        String[] parts = dateValue.split("/");
        int day = Integer.parseInt(parts[0]);
        WebElement dayCell = popup.findElement(By.xpath(".//td[not(contains(@class,'ant-picker-cell-disabled'))]//div[text()='" + day + "']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dayCell);
    }

    public String formatTanggal(String dateValue) {
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id", "ID"));
        return LocalDate.parse(dateValue, parser).format(formatter);
    }

    // SCENARIO TEST PENGAJUAN SPPD

    @Test (groups = "modal1")
    public void moveModalPengajuan1() {
        WebDriverWait wait = getWait(5);
        driver.findElement(By.cssSelector("a[href='/pengajuan']")).click();
        wait.until(ExpectedConditions.urlContains("/pengajuan"));
        Assert.assertEquals(driver.getCurrentUrl(), "https://esppd.pln.co.id/pengajuan");

        clickButtonByText("Tambah Pengajuan");

        WebElement modalTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("rcDialogTitle0")));
        Assert.assertEquals(modalTitle.getText(), "Pengajuan SPPD");

        selectDynamicDropdownByString("rc_select_1", "Jakarta Selatan");
        setTanggalRange("Tanggal Berangkat", "20/09/2025", "Tanggal Kembali", "25/09/2025");
        clickButtonByText("Selanjutnya");
    }

    @Test (groups = "modal2", dependsOnGroups = "modal1")
    public void moveModalPengajuan2() {
        selectDynamicDropdownByString("rc_select_3", "Kota Bandung");
        selectDynamicDropdownByString("rc_select_4", "IKROP Dalam Unit Induk (Perjalanan dinas ke ibu kota provinsi di dalam unit induk namun di luar area/regional/cabang)");
        selectDynamicDropdownByString("rc_select_5", "Perjalanan Dinas non Diklat");
        selectDynamicDropdownByString("rc_select_6", "Perjalanan Dinas Non-Diklat");

        WebElement textareaAgenda = getWait(5).until(ExpectedConditions.elementToBeClickable(
                By.xpath("//textarea[@placeholder='Masukkan Kegiatan/Agenda']")));
        textareaAgenda.sendKeys("Meeting di UID Bandung.");
        Assert.assertEquals(textareaAgenda.getAttribute("value"), "Meeting di UID Bandung.");

        WebElement textareaKeterangan = getWait(5).until(ExpectedConditions.elementToBeClickable(
                By.xpath("//textarea[@placeholder='Masukkan Keterangan']")));
        textareaKeterangan.sendKeys("Ini keterangan tambahan");
        Assert.assertEquals(textareaKeterangan.getAttribute("value"), "Ini keterangan tambahan");

        clickButtonByText("Selanjutnya");
    }

    @Test (groups = "modal3", dependsOnGroups = "modal2")
    public void moveModalPengajuan3() {
        WebDriverWait wait = getWait(5);

        WebElement accountingObject = driver.findElement(
                By.xpath("//div[text()='Accounting Object']/following::div[contains(@class,'ant-select-selector')][1]//span[contains(@class,'ant-select-selection-item')]"));
        String selectedValue = accountingObject.getText();
        Assert.assertFalse(selectedValue.isEmpty(), "Accounting object kosong!");
        System.out.println("Accounting Object: " + selectedValue);

        WebElement costAssigne = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[placeholder='Pilih Cost Assigne']")));
        String inputValue = costAssigne.getAttribute("value");
        Assert.assertFalse(inputValue.isEmpty(), "Cost Assigne belum terisi!");
        Assert.assertTrue(inputValue.matches("\\d+"), "Cost Assigne harus berupa angka!");
        System.out.println("Cost Assigne: " + inputValue);

        WebElement penyetuju = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[text()='Penyetuju']")));
        List<WebElement> penyetujuOptions = driver.findElements(
                By.xpath("//div[text()='Penyetuju']/following-sibling::div[string-length(normalize-space(text())) > 0]"));
        int count = penyetujuOptions.size();
        Assert.assertTrue(count >= 1 && count <= 2, "Jumlah Penyetuju tidak sesuai: " + count);
        System.out.println("Penyetuju (" + count + "):");
        for (int i = 0; i < count; i++) {
            String text = penyetujuOptions.get(i).getText().trim();
            System.out.println((i + 1) + ". " + text);
        }

        List<WebElement> changeButtons = driver.findElements(
                By.xpath("//button[.//span[text()='Change Cost Assigne']]")
        );
        WebElement btnChangeCost = null;
        for (WebElement btn : changeButtons) {
            if (btn.isDisplayed()) {
                btnChangeCost = btn;
                break;
            }
        }
        Assert.assertNotNull(btnChangeCost, "Button 'Change Cost Assigne' harus ada");
        Assert.assertEquals(btnChangeCost.getText().trim(), "Change Cost Assigne");

        WebElement buttonPreview = driver.findElement(
                By.xpath("//button[contains(@class,'ant-btn-primary')][.//span[text()='Preview']]"));
        Assert.assertTrue(buttonPreview.isDisplayed(), "Button 'Preview' sudah tidak disable");
        Assert.assertEquals(buttonPreview.getText().trim(), "Preview");

        clickButtonByText("Preview");
    }

    @Test (dependsOnGroups = "modal3")
    public void validatePreviewPengajuan () {
        WebDriverWait wait = getWait(5);

        WebElement previewPengajuan = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.ant-modal-body")));

        WebElement modalTitle = driver.findElement(By.xpath("//div[@class='ant-modal-header']//div[@class='ant-modal-title']"));
        Assert.assertEquals(modalTitle.getText().trim(), "Preview Pengajuan SPPD");

        Assert.assertEquals(previewPengajuan.findElement(By.xpath(".//div[text()='Jenis SPPD']/following-sibling::div")).getText(), "Employee");
        Assert.assertEquals(previewPengajuan.findElement(By.xpath(".//div[text()='Tanggal Berangkat']/following-sibling::div")).getText(), "20/09/2025");
        Assert.assertEquals(previewPengajuan.findElement(By.xpath(".//div[text()='Tanggal Kembali']/following-sibling::div")).getText(), "25/09/2025");
        Assert.assertEquals(previewPengajuan.findElement(By.xpath(".//div[text()='Kota Keberangkatan']/following-sibling::div")).getText(), "Jakarta Selatan");
        Assert.assertEquals(previewPengajuan.findElement(By.xpath(".//div[text()='Kota Tujuan']/following-sibling::div")).getText(), "Kota Bandung");
        Assert.assertEquals(previewPengajuan.findElement(By.xpath(".//div[text()='Country/Region']/following-sibling::div")).getText(),
                "IKROP Dalam Unit Induk (Perjalanan dinas ke ibu kota provinsi di dalam unit induk namun di luar area/regional/cabang)");
        Assert.assertEquals(previewPengajuan.findElement(By.xpath(".//div[text()='Aktivitas']/following-sibling::div")).getText(), "Perjalanan Dinas Non-Diklat");
        Assert.assertEquals(previewPengajuan.findElement(By.xpath(".//div[text()='Tipe Trip']/following-sibling::div")).getText(), "Perjalanan Dinas non Diklat");
        Assert.assertEquals(previewPengajuan.findElement(By.xpath(".//div[text()='Kegiatan/Agenda']/following-sibling::div")).getText(), "Meeting di UID Bandung.");
        Assert.assertEquals(previewPengajuan.findElement(By.xpath(".//div[text()='Keterangan']/following-sibling::div[1]")).getText(),"Ini keterangan tambahan");
        Assert.assertEquals(previewPengajuan.findElement(By.xpath(".//div[text()='Cost Assigne']/following-sibling::div")).getText(), "1012140100");
        WebElement penyetuju = previewPengajuan.findElement(
                By.xpath(".//div[text()='Penyetuju']/following-sibling::div[1]"));
        String penyetujuText = penyetuju.getText().trim();
        Assert.assertFalse(penyetujuText.isEmpty(), "Penyetuju takbole kosong");

        System.out.println("Preview Pengajuan SPPD sudah jos gandos.");

        clickButtonByText("Simpan"); //jangan coba-coba di Production
    }

}

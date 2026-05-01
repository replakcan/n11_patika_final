import { test, expect } from "@playwright/test";

test("public catalog renders", async ({ page }) => {
  await page.goto("/");
  await expect(page.getByRole("heading", { name: "Products" })).toBeVisible();
});

test("auth-protected cart redirects to sign in", async ({ page }) => {
  await page.goto("/cart");
  await expect(page).toHaveURL(/auth\/signin|api\/auth\/signin/);
});

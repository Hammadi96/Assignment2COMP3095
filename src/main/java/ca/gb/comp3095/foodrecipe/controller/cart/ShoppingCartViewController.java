package ca.gb.comp3095.foodrecipe.controller.cart;

import ca.gb.comp3095.foodrecipe.model.domain.Ingredient;
import ca.gb.comp3095.foodrecipe.model.service.RecipeService;
import ca.gb.comp3095.foodrecipe.model.service.ShoppingCartService;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ViewResolver;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import static ca.gb.comp3095.foodrecipe.view.AttributeTags.MESSAGE;
import static ca.gb.comp3095.foodrecipe.view.AttributeTags.SHOPPING_ITEMS;
import static ca.gb.comp3095.foodrecipe.view.AttributeTags.TOTAL_ITEMS;
import static ca.gb.comp3095.foodrecipe.view.AttributeTags.WARNING;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;

@Controller
@RequestMapping("/view/cart")
@Slf4j
public class ShoppingCartViewController {
    @Autowired
    RecipeService recipeService;

    @Autowired
    ShoppingCartService shoppingCartService;

    @Autowired
    ViewResolver viewResolver;

    @GetMapping
    public String getAllItems(final Principal principal, final Model model) {
        if (shoppingCartService.getTotal().equals(BigDecimal.ZERO)) {
            model.addAttribute(MESSAGE, "no items found !");
        } else {
            log.info("total items in cart are {}", shoppingCartService.getItemsInCart());
            log.info("{} are {}", TOTAL_ITEMS, shoppingCartService.getTotal().longValue());
            model.addAttribute(TOTAL_ITEMS, shoppingCartService.getTotal().longValue());
            model.addAttribute(SHOPPING_ITEMS, shoppingCartService.getItemsInCart());
            model.addAttribute(MESSAGE, shoppingCartService.getTotal().longValue() + " items in your shopping cart!");
        }
        return "cart/shopping-cart";
    }

    @GetMapping("/reduceItem/{item}")
    public ResponseEntity<String> reduceItem(@PathVariable("item") final String item, final Principal principal, final Model model) {
        if (shoppingCartService.getTotal().equals(BigDecimal.ZERO)) {
            return ResponseEntity.noContent().build();
        } else {
            Map<String, Integer> itemsInCart = shoppingCartService.getItemsInCart();
            if (!itemsInCart.containsKey(item)) {
                model.addAttribute(WARNING, "item " + item + " does not exist in the carr!");
                return ResponseEntity.notFound().build();
            } else {
                log.info("total items in cart are {}", itemsInCart);
                shoppingCartService.removeItem(item);
                return ResponseEntity.accepted().build();
            }
        }
    }

    @GetMapping("/removeItem/{item}")
    public ResponseEntity<String> removeItem(@PathVariable("item") final String item, final Principal principal, final Model model) {
        if (shoppingCartService.getTotal().equals(BigDecimal.ZERO)) {
            return ResponseEntity.noContent().build();
        } else {
            Map<String, Integer> itemsInCart = shoppingCartService.getItemsInCart();
            if (!itemsInCart.containsKey(item)) {
                log.warn("item {} not found in cart to remove", item);
                return ResponseEntity.notFound().build();
            } else {
                log.info("total items in cart are {}", itemsInCart);
                shoppingCartService.deleteItem(item);
                return ResponseEntity.accepted().build();
            }
        }
    }

    @GetMapping("/increaseItem/{item}")
    public ResponseEntity<String> increaseItem(@PathVariable("item") final String item, final Principal principal, final Model model) {
        if (shoppingCartService.getTotal().equals(BigDecimal.ZERO)) {
            return ResponseEntity.noContent().build();
        } else {
            Map<String, Integer> itemsInCart = shoppingCartService.getItemsInCart();
            if (!itemsInCart.containsKey(item)) {
                log.warn("item {} not found in cart to increase", item);
                return ResponseEntity.notFound().build();
            } else {
                log.info("total items in cart are {}", itemsInCart);
                shoppingCartService.addItem(item);
                return ResponseEntity.accepted().build();
            }
        }
    }


    @GetMapping(value = "/pdf", produces = APPLICATION_PDF_VALUE)
    public @ResponseBody
    ResponseEntity<byte[]> generatePdf(Model model, HttpServletRequest request) {
        try {
//            View view = viewResolver.resolveViewName("cart/shopping-cart", Locale.US);
//            MockHttpServletResponse mockResp = new MockHttpServletResponse();
//            view.render(model.asMap(), request, mockResp);
//            log.info("resolved item is \n{}", mockResp.getContentAsString());
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, os);
            document.open();
            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            font.setSize(18);
            font.setColor(Color.gray);

            Paragraph p = new Paragraph("List of Items", font);
            p.setAlignment(Paragraph.ALIGN_CENTER);

            document.add(p);
            Map<String, Integer> itemsInCart = shoppingCartService.getItemsInCart();


            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100f);
            table.setWidths(new float[]{1.5f, 3.5f, 3.0f});
            table.setSpacingBefore(10);


            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(Color.WHITE);
            cell.setPadding(5);

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA);
            headerFont.setColor(Color.LIGHT_GRAY);

            cell.setPhrase(new Phrase("#", font));

            table.addCell(cell);

            cell.setPhrase(new Phrase("Item", font));
            table.addCell(cell);

            cell.setPhrase(new Phrase("Quantity", font));
            table.addCell(cell);

            int count = 1;
            for (String item : itemsInCart.keySet()) {
                table.addCell(String.valueOf(count));
                table.addCell(item);
                table.addCell(String.valueOf(itemsInCart.get(item)));
                ++count;
            }
            document.add(table);
            document.close();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "file.pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            ResponseEntity<byte[]> response = new ResponseEntity<>(os.toByteArray(), headers, HttpStatus.OK);

            return response;
        } catch (Exception e) {
            log.warn("unable to print pdf at the moment !", e);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/addItem/{recipe-id}/{ingredient-id}")
    public ResponseEntity<?> addItem(@PathVariable("recipe-id") final Long recipeId, @PathVariable("ingredient-id") final Long ingId, final Model model) {
        log.info("recipeid {}, ing id {}", recipeId, ingId);
        // todo receive ing id
        return recipeService.getRecipeById(recipeId)
                .map(recipe -> {
                    Optional<Ingredient> firstIngredient = recipe.getAllIngredients().stream().filter(i -> i.getId().equals(ingId)).findFirst();
                    firstIngredient.ifPresent(ingredient -> {
                        log.info("adding ingredients {} to shopping list for recipe {}", ingredient, recipe.getId());
                        shoppingCartService.addItem(ingredient.getName());
                    });
                    return ResponseEntity.ok("Success!");
                }).orElse(ResponseEntity.notFound().build());
    }
}

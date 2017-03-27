package org.jimmutable.core.serialization;

import org.jimmutable.core.examples.book.Book;
import org.jimmutable.core.examples.book.BookDeckList;
import org.jimmutable.core.examples.book.BookDeckMap;
import org.jimmutable.core.examples.book.BookDeckSet;
import org.jimmutable.core.examples.product_data.ItemKey;
import org.jimmutable.core.examples.product_data.ItemSpecifications;
import org.jimmutable.core.serialization.reader.ObjectParseTree;


/**
 * Before a TypeName may be read, its corresponding class must be registered via
 * a call to ObjectParseTree.registerTypeName. To achieve this, main methods
 * will typically have their first statement be something like
 * MyPackageRegister.registerAllTypes();
 * 
 * If your package depends on other packages (almost always the case) you
 * implementation of registerAllTypes() should include class to the
 * TypeNameRegister of all pacakges on which you depend. (Don't worry, it is
 * safe to register one TypeName multiple times -- the last one simply wins)
 * 
 * Every TypeNameRegister should call
 * JimmutableTypeNameRegister.registerAllTypes() (for obvious reasons: you need
 * the jimmutable core types to be registered to do anything)
 * 
 * @author jim.kane
 *
 */
public class JimmutableTypeNameRegister
{
	static public void registerAllTypes()
	{
		ObjectParseTree.registerTypeName(FieldName.class);
		ObjectParseTree.registerTypeName(TypeName.class);
		
		ObjectParseTree.registerTypeName(Book.class);
		ObjectParseTree.registerTypeName(BookDeckList.class);
		ObjectParseTree.registerTypeName(BookDeckMap.class);
		ObjectParseTree.registerTypeName(BookDeckSet.class);
		
		ObjectParseTree.registerTypeName(ItemKey.class);
		ObjectParseTree.registerTypeName(ItemSpecifications.class);
	}
}

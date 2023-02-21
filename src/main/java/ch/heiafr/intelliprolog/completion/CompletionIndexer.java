package ch.heiafr.intelliprolog.completion;

import ch.heiafr.intelliprolog.psi.*;
import ch.heiafr.intelliprolog.reference.ReferenceHelper;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static java.util.Arrays.asList;

public class CompletionIndexer {

    private static Set<String> atomIndex = new HashSet<>();
    private static Set<String> compoundIndex = new HashSet<>();
    private static Set<String> variableIndex = new HashSet<>();

    public static void matchForCompletion(@NotNull CompletionParameters parameters,
                                          @NotNull ProcessingContext context,
                                          @NotNull CompletionResultSet result) {
        //Build index from caret position => find unique atoms, compounds and variables present in the context

        makeIndex(parameters.getOriginalFile(), parameters.getOriginalPosition());


        atomIndex.forEach(atom -> result.addElement(LookupElementBuilder.create(atom).withTypeText("Atom")));

        compoundIndex.forEach(compound -> result.addElement(LookupElementBuilder.create(compound).withTypeText("Compound").withInsertHandler(CompletionIndexer::handleInsert)));

        variableIndex.forEach(variable -> result.addElement(LookupElementBuilder.create(variable).withTypeText("Variable")));

        PREDEFINED_PREDICATES.forEach(name -> result.addElement(LookupElementBuilder.create(name).withTypeText("Predefined").withInsertHandler(CompletionIndexer::handleInsert)));
    }

    private static void handleInsert(@NotNull InsertionContext ctx, @NotNull LookupElement elt) {
        if (elt.getLookupString().contains("/")) {
            //Compound

            String name = elt.getLookupString().split("/")[0];
            int arity = Integer.parseInt(elt.getLookupString().split("/")[1]);


            ctx.getDocument().deleteString(ctx.getStartOffset() + name.length(), ctx.getTailOffset());
            ctx.getDocument().insertString(ctx.getStartOffset() + name.length(), "(");
            for (int i = 0; i < arity; i++) {
                ctx.getDocument().insertString(ctx.getTailOffset(), "_");
                if (i < arity - 1) {
                    ctx.getDocument().insertString(ctx.getTailOffset(), ", ");
                }
            }
            ctx.getDocument().insertString(ctx.getTailOffset(), ")");


            //More confortable to have the cursor at the end of the compound
            ctx.getEditor().getCaretModel().moveToOffset(ctx.getTailOffset());
        }
    }


    private static void makeIndex( PsiElement rootFile, PsiElement caret) {

        var copyFile = removeErrorForIndexing(rootFile);

        if (copyFile == null) {
            return; //Impossible to index => use previous index if any...
        }

        atomIndex = new HashSet<>();
        compoundIndex = new HashSet<>();
        variableIndex = new HashSet<>();

        var files = ReferenceHelper.findEveryImportedFile(rootFile);
        files.add(copyFile); //Add root file to the list of files to parse

        for (var psiFile : files) {

            //Search all atoms or compounds in the file and imported files
            PsiTreeUtil.findChildrenOfType(psiFile, PrologAtom.class).stream()
                    .filter(atom -> atom.getLastChild().getNode().getElementType().equals(PrologTypes.UNQUOTED_ATOM))
                    .map(PsiNamedElement::getName)
                    .forEach(atomIndex::add);

            PsiTreeUtil.findChildrenOfType(psiFile, PrologCompound.class).stream()
                    .map(CompletionIndexer::compoundToString)
                    .forEach(compoundIndex::add);
        }


        //Search all variables in the current sentence
        PrologSentence sentence = PsiTreeUtil.getParentOfType(caret, PrologSentence.class);
        if (sentence != null) {
            PsiTreeUtil.findChildrenOfType(sentence, PrologVariable.class).stream()
                    //Don't care about anonymous variables
                    .filter(atom -> atom.getLastChild().getNode().getElementType().equals(PrologTypes.NAMED_VARIABLE))
                    .map(PsiNamedElement::getName)
                    .forEach(variableIndex::add);
        }
    }

    private static PsiElement removeErrorForIndexing(PsiElement rootFile) {
        PsiElement copy = rootFile.copy();
        var error = PsiTreeUtil.findChildOfType(copy, PsiErrorElement.class);

        if (error == null) {
            return copy;
        }


        var parent = error.getParent();

        boolean deleteParent = false;
        //Find all children of the parent of the error
        PsiElement lastUsable = null;
        for (var child : parent.getChildren()) {
            //If the child is an error, delete it
            if (child instanceof PsiWhiteSpace || child instanceof PsiComment) {
                continue;
            }
            if (child instanceof PsiErrorElement) {
                if (lastUsable != null) {
                    lastUsable.delete();
                } else {
                    deleteParent = true;
                }
                break;
            }
            lastUsable = child;
        }
        if (deleteParent) {
            if (Objects.equals(parent, copy)) {
                return null; //Impossible to delete the root file
            }
            parent.delete();
        }

        try {
            copy = PrologElementFactory.rebuildTree(copy);
            return copy;
        } catch (Exception e) {
            //More than one error in the file => impossible to rebuild the tree
            return null;
        }
    }

    private static String compoundToString(PrologCompound prologCompound) {
        String name = prologCompound.getCompoundName().getText();
        int arity = ReferenceHelper.getArity(prologCompound);
        return name + "/" + arity;
    }

    // Taken from http://www.gprolog.org/manual/gprolog.html#%40index
    // with some dirty (and semi-manual) pre-processing
    // (some predicates have many arities (eg call/2-11), I kept only 2-3...)
    private static final Set<String> PREDEFINED_PREDICATES = new HashSet<>(asList(
            "abolish/1",
            "abort",
            "absolute_file_name/2",
            "acyclic_term/1",
            "add_linedit_completion/1",
            "add_stream_alias/2",
            "add_stream_mirror/2",
            "append/1",
            "append/3",
            "architecture/1",
            "arg/3",
            "argument_counter/1",
            "argument_list/1",
            "argument_value/2",
            "asserta/1",
            "assertz/1",
            "at_end_of_stream",
            "at_end_of_stream/1",
            "atom/1",
            "atom_chars/2",
            "atom_codes/2",
            "atom_concat/3",
            "atom_length/2",
            "atom_property/2",
            "atomic/1",
            "bagof/3",
            "between/3",
            "bind_variables/2",
            "break",
            "built_in",
            "built_in/1",
            "built_in_fd",
            "built_in_fd/1",
            "call/1",
            "call/2",
            "call/3",
            "call_det/2",
            "call_with_args/1",
            "call_with_args/2",
            "call_with_args/3",
            "callable/1",
            "catch/3",
            "change_directory/1",
            "char_code/2",
            "char_conversion/2",
            "char_conversion/2",
            "character_count/2",
            "clause/2",
            "close/1",
            "close/2",
            "close_input_atom_stream/1",
            "close_input_chars_stream/1",
            "close_input_codes_stream/1",
            "close_output_atom_stream/2",
            "close_output_chars_stream/2",
            "close_output_codes_stream/2",
            "compare/3",
            "compound/1",
            "consult/1",
            "copy_term/2",
            "cpu_time/1",
            "create_pipe/2",
            "current_alias/2",
            "current_atom/1",
            "current_bip_name/2",
            "current_char_conversion/2",
            "current_input/1",
            "current_mirror/2",
            "current_op/3",
            "current_output/1",
            "current_predicate/1",
            "current_prolog_flag/2",
            "current_stream/1",
            "date_time/1",
            "debug",
            "debugging",
            "decompose_file_name/4",
            "delete/3",
            "delete_directory/1",
            "delete_file/1",
            "directory_files/2",
            "discontiguous/1",
            "display/1",
            "display/2",
            "display_to_atom/2",
            "display_to_chars/2",
            "display_to_codes/2",
            "dynamic/1",
            "elif/1",
            "else",
            "endif",
            "ensure_linked/1",
            "ensure_loaded/1",
            "environ/2",
            "exec/4",
            "exec/5",
            "expand_term/2",
            "fail",
            "false",
            "fd_all_different/1",
            "fd_at_least_one/1",
            "fd_at_most_one/1",
            "fd_atleast/3",
            "fd_atmost/3",
            "fd_cardinality/2",
            "fd_cardinality/3",
            "fd_dom/2",
            "fd_domain/2",
            "fd_domain/3",
            "fd_domain_bool/1",
            "fd_element/3",
            "fd_element_var/3",
            "fd_exactly/3",
            "fd_has_extra_cstr/1",
            "fd_has_vector/1",
            "fd_labeling/1",
            "fd_labeling/2",
            "fd_labelingff/1",
            "fd_max/2",
            "fd_max_integer/1",
            "fd_maximize/2",
            "fd_min/2",
            "fd_minimize/2",
            "fd_not_prime/1",
            "fd_only_one/1",
            "fd_prime/1",
            "fd_reified_in/4",
            "fd_relation/2",
            "fd_relationc/2",
            "fd_set_vector_max/1",
            "fd_size/2",
            "fd_use_vector/1",
            "fd_var/1",
            "fd_vector_max/1",
            "file_exists/1",
            "file_permission/2",
            "file_property/2",
            "find_linedit_completion/2",
            "findall/3",
            "findall/4",
            "flatten/2",
            "float/1",
            "flush_output",
            "flush_output/1",
            "for/3",
            "forall/2",
            "foreign/1",
            "foreign/2",
            "fork_prolog/1",
            "format/2",
            "format/3",
            "format_to_atom/3",
            "format_to_chars/3",
            "format_to_codes/3",
            "functor/3",
            "g_array_size/2",
            "g_assign/2",
            "g_assignb/2",
            "g_dec/1",
            "g_dec/2",
            "g_dec/3",
            "g_deco/2",
            "g_inc/1",
            "g_inc/2",
            "g_inc/3",
            "g_inco/2",
            "g_link/2",
            "g_read/2",
            "g_reset_bit/2",
            "g_set_bit/2",
            "g_test_reset_bit/2",
            "g_test_set_bit/2",
            "generic_var/1",
            "get/1",
            "get0/1",
            "get_byte/1",
            "get_byte/2",
            "get_char/1",
            "get_char/2",
            "get_code/1",
            "get_code/2",
            "get_key/1",
            "get_key/2",
            "get_key_no_echo/1",
            "get_key_no_echo/2",
            "get_linedit_prompt/1",
            "get_print_stream/1",
            "get_seed/1",
            "ground/1",
            "halt",
            "halt/1",
            "host_name/1",
            "hostname_address/2",
            "if/1",
            "include/1",
            "initialization/1",
            "integer/1",
            "is_absolute_file_name/1",
            "is_list/1",
            "is_relative_file_name/1",
            "keysort/1",
            "keysort/2",
            "last/2",
            "last_read_start_line_column/2",
            "leash/1",
            "length/2",
            "line_count/2",
            "line_position/2",
            "list/1",
            "list_or_partial_list/1",
            "listing",
            "listing/1",
            "load/1",
            "lower_upper/2",
            "make_directory/1",
            "maplist/2",
            "maplist/3",
            "maplist/4",
            "max_list/2",
            "member/2",
            "memberchk/2",
            "min_list/2",
            "msort/1",
            "msort/2",
            "multifile/1",
            "name/2",
            "name_query_vars/2",
            "name_singleton_vars/1",
            "new_atom/1",
            "new_atom/2",
            "nl",
            "nl/1",
            "nodebug",
            "non_fd_var/1",
            "non_generic_var/1",
            "nonvar/1",
            "nospy/1",
            "nospyall",
            "notrace",
            "nth/3",
            "number/1",
            "number_atom/2",
            "number_chars/2",
            "number_codes/2",
            "numbervars/1",
            "numbervars/3",
            "once/1",
            "op/3",
            "op/3",
            "open/3",
            "open/4",
            "open_input_atom_stream/2",
            "open_input_chars_stream/2",
            "open_input_codes_stream/2",
            "open_output_atom_stream/1",
            "open_output_chars_stream/1",
            "open_output_codes_stream/1",
            "os_version/1",
            "partial_list/1",
            "peek_byte/1",
            "peek_byte/2",
            "peek_char/1",
            "peek_char/2",
            "peek_code/1",
            "peek_code/2",
            "permutation/2",
            "phrase/2",
            "phrase/3",
            "popen/3",
            "portray/1",
            "portray_clause/1",
            "portray_clause/2",
            "predicate_property/2",
            "prefix/2",
            "print/1",
            "print/2",
            "print_to_atom/2",
            "print_to_chars/2",
            "print_to_codes/2",
            "prolog_file_name/2",
            "prolog_pid/1",
            "public/1",
            "put/1",
            "put_byte/1",
            "put_byte/2",
            "put_char/1",
            "put_char/2",
            "put_code/1",
            "put_code/2",
            "random/1",
            "random/3",
            "randomize",
            "read/1",
            "read/2",
            "read_atom/1",
            "read_atom/2",
            "read_from_atom/2",
            "read_from_chars/2",
            "read_from_codes/2",
            "read_integer/1",
            "read_integer/2",
            "read_number/1",
            "read_number/2",
            "read_pl_state_file/1",
            "read_term/2",
            "read_term/3",
            "read_term_from_atom/3",
            "read_term_from_chars/3",
            "read_term_from_codes/3",
            "read_token/1",
            "read_token/2",
            "read_token_from_atom/2",
            "read_token_from_chars/2",
            "read_token_from_codes/2",
            "real_time/1",
            "remove_stream_mirror/2",
            "rename_file/2",
            "repeat",
            "retract/1",
            "retractall/1",
            "reverse/2",
            "see/1",
            "seeing/1",
            "seek/4",
            "seen",
            "select/3",
            "select/5",
            "send_signal/2",
            "set_bip_name/2",
            "set_input/1",
            "set_linedit_prompt/1",
            "set_output/1",
            "set_prolog_flag/2",
            "set_prolog_flag/2",
            "set_seed/1",
            "set_stream_buffering/2",
            "set_stream_eof_action/2",
            "set_stream_line_column/3",
            "set_stream_position/2",
            "set_stream_type/2",
            "setarg/3",
            "setarg/4",
            "setof/3",
            "shell",
            "shell/1",
            "shell/2",
            "skip/1",
            "sleep/1",
            "socket/2",
            "socket_accept/3",
            "socket_accept/4",
            "socket_bind/2",
            "socket_close/1",
            "socket_connect/4",
            "socket_listen/2",
            "sort/1",
            "sort/2",
            "spawn/2",
            "spawn/3",
            "spy/1",
            "spypoint_condition/3",
            "statistics",
            "statistics/2",
            "stop",
            "stream_line_column/3",
            "stream_position/2",
            "stream_property/2",
            "sub_atom/5",
            "sublist/2",
            "subsumes_term/2",
            "subtract/3",
            "succ/2",
            "suffix/2",
            "sum_list/2",
            "syntax_error_info/4",
            "system/1",
            "system/2",
            "system_time/1",
            "tab/1",
            "tell/1",
            "telling/1",
            "temporary_file/3",
            "temporary_name/2",
            "term_expansion/2",
            "term_hash/2",
            "term_hash/4",
            "term_ref/2",
            "term_variables/2",
            "term_variables/3",
            "throw/1",
            "told",
            "top_level",
            "trace",
            "true",
            "unget_byte/1",
            "unget_byte/2",
            "unget_char/1",
            "unget_char/2",
            "unget_code/1",
            "unget_code/2",
            "unify_with_occurs_check/2",
            "unlink/1",
            "user_time/1",
            "var/1",
            "wait/2",
            "wam_debug",
            "working_directory/1",
            "write/1",
            "write/2",
            "write_canonical/1",
            "write_canonical/2",
            "write_canonical_to_atom/2",
            "write_canonical_to_chars/2",
            "write_canonical_to_codes/2",
            "write_pl_state_file/1",
            "write_term/2",
            "write_term/3",
            "write_term_to_atom/3",
            "write_term_to_chars/3",
            "write_term_to_codes/3",
            "write_to_atom/2",
            "write_to_chars/2",
            "write_to_codes/2",
            "writeq/1",
            "writeq/2",
            "writeq_to_atom/2",
            "writeq_to_chars/2",
            "writeq_to_codes/2"
    ));

}

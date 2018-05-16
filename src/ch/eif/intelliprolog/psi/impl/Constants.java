package ch.eif.intelliprolog.psi.impl;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * Implementation note:
 * This should be a temporary solution only.
 * Constants hardcoded in this class should rather be dynamic, obtained from a proper Prolog query.
 */
public class Constants {

    public static final Set<String> KEYWORDS = new HashSet<>(asList("dynamic", "public", "multifile", "discontiguous",  //newHashSet
            "ensure_linked", "built_in", "built_in_fd", "include", "ensure_loaded", "op", "char_conversion",
            "set_prolog_flag", "initialization", "foreign", "fd_max_integer", "fd_vector_max", "fd_set_vector_max",
            "fd_domain", "fd_domain_bool", "fd_var", "non_fd_var", "generic_var", "non_generic_var", "fd_min", "fd_max",
            "fd_size", "fd_dom", "fd_has_extra_cstr", "fd_has_vector", "fd_use_vector", "fd_prime", "fd_not_prime",
            "fd_cardinality", "fd_cardinality", "fd_at_least_one", "fd_at_most_one", "fd_only_one", "fd_all_different",
            "fd_element", "fd_element_var", "fd_atmost", "fd_atleast", "fd_exactly", "fd_relation", "fd_relationc",
            "fd_labeling", "fd_labelingff", "fd_minimize", "fd_maximize", "var", "nonvar", "atom", "integer", "float",
            "number", "atomic", "compound", "callable", "list", "partial_list", "list_or_partial_list",
            "unify_with_occurs_check", "compare", "functor", "arg", "copy_term", "setarg", "name_singleton_vars",
            "name_query_vars", "bind_variables", "numbervars", "term_ref", "asserta", "assertz", "retract",
            "retractall", "clause", "abolish", "current_predicate", "predicate_property", "findall", "bagof", "setof",
            "current_input", "current_output", "set_input", "set_output", "open", "open", "close", "close",
            "flush_output", "current_stream", "stream_property", "at_end_of_stream", "at_end_of_stream",
            "stream_position", "set_stream_position", "seek", "character_count", "line_count", "line_position",
            "stream_line_column", "set_stream_line_column", "add_stream_alias", "current_alias", "add_stream_mirror",
            "remove_stream_mirror", "current_mirror", "set_stream_type", "set_stream_eof_action", "set_stream_buffering",
            "open_input_atom_stream", "open_input_chars_stream", "open_input_codes_stream", "close_input_atom_stream",
            "close_input_chars_stream", "close_input_codes_stream", "open_output_atom_stream", "open_output_chars_stream",
            "open_output_codes_stream", "close_output_atom_stream", "close_output_chars_stream", "close_output_codes_stream",
            "get_char", "get_code", "get_key", "get_key_no_echo", "peek_char", "peek_code", "unget_char", "unget_code",
            "put_char", "put_code", "nl", "get_byte", "peek_byte", "unget_byte", "put_byte", "read_term", "read",
            "read_atom", "read_integer", "read_number", "read_token", "syntax_error_info", "last_read_start_line_column",
            "write_term", "write", "writeq", "write_canonical", "display", "print", "format", "portray_clause",
            "get_print_stream", "op", "current_op", "char_conversion", "current_char_conversion", "read_term_from_atom",
            "read_from_atom", "read_token_from_atom", "read_term_from_chars", "read_from_chars", "read_token_from_chars",
            "read_term_from_codes", "read_from_codes", "read_token_from_codes", "write_term_to_atom", "write_to_atom",
            "writeq_to_atom", "write_canonical_to_atom", "display_to_atom", "print_to_atom", "format_to_atom",
            "write_term_to_chars", "write_to_chars", "writeq_to_chars", "write_canonical_to_chars", "display_to_chars",
            "print_to_chars", "format_to_chars", "write_term_to_codes", "write_to_codes", "writeq_to_codes",
            "write_canonical_to_codes", "display_to_codes", "print_to_codes", "format_to_codes", "see", "tell", "append",
            "seeing", "telling", "seen", "told", "get0", "get", "skip", "put", "tab", "expand_term", "term_expansion",
            "phrase", "abort", "stop", "top_level", "break", "halt", "once", "call_with_args-11", "call", "repeat",
            "for", "atom_length", "atom_concat", "sub_atom", "char_code", "lower_upper", "atom_chars", "atom_codes",
            "number_atom", "number_chars", "number_codes", "name", "atom_hash", "new_atom", "current_atom",
            "atom_property", "append", "member", "memberchk", "reverse", "delete", "select", "permutation", "prefix",
            "suffix", "sublist", "last", "length", "nth", "max_list", "min_list", "sum_list", "sort", "sort0", "keysort",
            "g_assign", "g_assignb", "g_link", "g_read", "g_array_size", "g_inc", "g_inco", "g_dec", "g_deco", "g_set_bit",
            "g_reset_bit", "g_test_set_bit", "g_test_reset_bit", "set_prolog_flag", "current_prolog_flag", "set_bip_name",
            "current_bip_name", "write_pl_state_file", "read_pl_state_file", "consult", "load", "listing", "statistics",
            "user_time", "system_time", "cpu_time", "real_time", "set_seed", "randomize", "get_seed", "random", "random",
            "absolute_file_name", "decompose_file_name", "prolog_file_name", "argument_counter", "argument_value",
            "argument_list", "environ", "make_directory", "delete_directory", "change_directory", "working_directory",
            "directory_files", "rename_file", "delete_file", "unlink", "file_permission", "file_exists", "file_property",
            "temporary_name", "temporary_file", "date_time", "host_name", "os_version", "architecture", "shell", "system",
            "spawn", "spawn", "popen", "exec", "fork_prolog", "create_pipe", "wait", "prolog_pid", "send_signal", "sleep",
            "select", "socket", "socket_close", "socket_bind", "socket_connect", "socket_listen", "socket_accept",
            "socket_accept", "hostname_address", "get_linedit_prompt", "set_linedit_prompt", "add_linedit_completion",
            "find_linedit_completion", "sr_open", "sr_change_options", "sr_close", "sr_read_term", "sr_current_descriptor",
            "sr_get_stream", "sr_get_module", "sr_get_file_name", "sr_get_position", "sr_get_include_list",
            "sr_get_include_stream_list", "sr_get_size_counters", "sr_get_error_counters", "sr_set_error_counters",
            "sr_error_from_exception", "sr_write_message", "sr_write_error"));

}

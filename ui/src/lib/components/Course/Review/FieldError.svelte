<script lang="ts">
    import {ErrorMessage} from "sveltik";
    export let name: string;
</script>

<div class='text-sm italic text-red-400'>
    <ErrorMessage name={name} />
  </div>
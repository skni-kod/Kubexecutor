'use client';

import React, {useRef, useState} from "react";
import Editor, { Monaco } from "@monaco-editor/react";
import type { editor as EditorType } from "monaco-editor";
import { ChevronDownIcon } from "@heroicons/react/20/solid";

const options = [
    { value: "nodejs", label: "JavaScript" },
    { value: "java", label: "Java" },
    { value: "python", label: "Python" },
]

export type CodeEditorProps = {
    authToken: string | null
}

const CodeEditor = ({ authToken }: CodeEditorProps) => {
    const editorRef = useRef<EditorType.IStandaloneCodeEditor | null>(null);
    const [language, setLanguage] = useState("nodejs");
    const [monacoInstance, setMonacoInstance] = useState<Monaco | null>(null);
    const [output, setOutput] = useState<{ stdOut: string; stdErr: string; exitCode: number } | null>(null);
    const [error, setError] = useState<{ message: string } | null>(null);
    const endpoint = "/api/execute"

    const getValue = async () => {
        const base64Code = convertToBase64(editorRef.current?.getValue() || '');
        let response = await fetch(endpoint, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                language: language,
                base64Code: base64Code,
                authToken: authToken
            }
            )
        })
        if (response.status == 401) {
            console.log('redirect')
            signIn()
        } else if (response.status == 200) {
            response.json()
                .then(data => {
                    console.log(data)
                    setOutput(data)
                    setError(null)
                });
        } else if (response.status == 503) {
            response.json()
                .then(data => {
                    console.log(data);
                    setOutput(null)
                    setError(data)
                });
        }
    }

    const signIn = async () => {
        const endpoint = "/api/authenticationUrl"
        fetch(endpoint, {
            method: 'GET'
        }).then(response => response.json())
            .then(data => window.location.replace(data.url))
    };

    const convertToBase64 = (str: string) => {
        const buff = Buffer.from(str, 'utf-8');
        return buff.toString('base64');
    }

    const handleEditorDidMount = (
        editor: EditorType.IStandaloneCodeEditor,
        monaco: Monaco
    ) => {
        editorRef.current = editor;
        setMonacoInstance(monaco);
    };

    const handleLanguageChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const model = editorRef.current?.getModel();
        if (model && monacoInstance) {
            if (e.target.value === "nodejs") {
                monacoInstance.editor.setModelLanguage(model, "javascript");
            } else monacoInstance.editor.setModelLanguage(model, e.target.value);
            setLanguage(e.target.value)
        }
    };

    return (
        <div className="flex flex-col h-screen">
            <div className="flex items-center justify-center space-x-4 p-4">
                <button
                    className="text-white font-bold py-2 px-4 rounded bg-blue-500 hover:bg-blue-600"
                    onClick={getValue}
                >
                    Submit
                </button>
                <div className="relative inline-flex">
                    <select
                        className="appearance-none border border-gray-600 shadow bg-gray-900 text-white font-bold py-2 pl-4 pr-8 rounded cursor-pointer focus:outline-none focus:ring-2 focus:ring-blue-500"
                        onChange={handleLanguageChange}
                    >
                        {options.map((option) => (
                            <option key={option.value} value={option.value}>
                                {option.label}
                            </option>
                        ))}
                    </select>
                    <ChevronDownIcon
                        className="absolute top-1/2 right-2 transform -translate-y-1/2 w-5 h-5 text-white pointer-events-none" />
                </div>
            </div>
            <Editor
                height="70vh"
                defaultLanguage="javascript"
                defaultValue={typeof window !== "undefined" ? localStorage.getItem("code") || '// Write your code here' : "// Write your code here"}
                theme="vs-dark"
                onMount={handleEditorDidMount}
                className="flex-grow"
                onChange={(value) => localStorage.setItem('code', value || '')}
            />
            {output && (
                <div className="p-4 bg-gray-900 text-white">
                    <h1>Result</h1>
                    <h2 className="font-bold">Output:</h2>
                    <pre className="whitespace-pre-wrap">{output.stdOut}</pre>
                    <h2 className="font-bold">Error:</h2>
                    <pre className="whitespace-pre-wrap">{output.stdErr}</pre>
                    <h2 className="font-bold">Exit Code:</h2>
                    <pre className="whitespace-pre-wrap">{output.exitCode}</pre>
                </div>
            )}
            {error && !output && (
                <div className="p-4 bg-gray-900 text-white">
                    <h1>Error</h1>
                    <h2 className="font-bold">Message:</h2>
                    <pre className="whitespace-pre-wrap">{error.message}</pre>
                </div>
            )}
        </div>
    );
};

export default CodeEditor;
// This file is now fully dynamic and uses Vite's import.meta.glob
// It will automatically update whenever you add, rename, or modify files in the prompts/ folder.

export interface PromptFile {
  name: string;
  fileName: string;
  type: 'pro' | 'free';
  content: string;
  description?: string;
}

export interface PromptFolder {
  name: string;
  files: PromptFile[];
}

// Utility to get display name from filename
const getDisplayName = (fileName: string) => {
  return fileName
    .replace(/\.md$/, '')
    .replace(/_pro$/, '')
    .replace(/_free$/, '')
    .replace(/-/g, ' ')
    .replace(/_/g, ' ')
    .replace(/\b\w/g, (c) => c.toUpperCase());
};

// Utility to extract description from HTML comment and strip it from content
const extractDescription = (rawContent: string): { description?: string; content: string } => {
  const match = rawContent.match(/^<!--\s*DESCRIPTION:\s*(.+?)\s*-->/);
  if (!match) return { content: rawContent };

  const rawDesc = match[1].trim();
  // Clamp description: min 2 lines, max 5 lines worth of text
  // Split into sentences for cleaner line breaks
  const sentences = rawDesc.split(/(?<=[.!?])\s+/).filter(Boolean);
  let description = rawDesc;
  if (sentences.length > 5) {
    description = sentences.slice(0, 5).join(' ');
  }

  // Strip the description comment from content
  const content = rawContent.replace(/^<!--\s*DESCRIPTION:.+?-->\s*\n?/, '').trimStart();

  return { description, content };
};

// Utility to get file type (pro or free) from filename
const getFileType = (fileName: string): 'pro' | 'free' => {
  const baseName = fileName.replace(/\.prompt.md$/, '');
  if (baseName.endsWith('-free')) return 'free';
  return 'pro';
};

// Load all markdown files from the root prompts directory
const rawPrompts = import.meta.glob('../../../prompts/**/*.prompt.md', { query: '?raw', eager: true }) as Record<
  string,
  { default: string }
>;

// Load AGENTS.md specifically
const agentsFile = import.meta.glob('../../../AGENTS.md', { query: '?raw', eager: true }) as Record<string, { default: string }>;

export interface ExplorerData {
  prompts: PromptFolder[];
  agents?: PromptFile;
}

const processPrompts = (): PromptFolder[] => {
  const foldersMap: Record<string, PromptFile[]> = {};

  Object.entries(rawPrompts).forEach(([path, module]) => {
    const segments = path.split('/');
    const fileName = segments.pop() || '';
    const folderName = segments.pop() || '';

    if (fileName === '_template.md' || fileName === 'README.md' || folderName === 'prompts') return;

    const { description, content } = extractDescription(module.default);

    const file: PromptFile = {
      name: getDisplayName(fileName),
      fileName: fileName,
      type: getFileType(fileName),
      content,
      description
    };

    if (!foldersMap[folderName]) {
      foldersMap[folderName] = [];
    }
    foldersMap[folderName].push(file);
  });

  return Object.entries(foldersMap)
    .map(([name, files]) => ({
      name,
      files: files.sort((a, b) => {
        // Free files come first within each folder
        if (a.type === 'free' && b.type !== 'free') return -1;
        if (a.type !== 'free' && b.type === 'free') return 1;
        return a.name.localeCompare(b.name);
      })
    }))
    .sort((a, b) => a.name.localeCompare(b.name));
};

const processedPrompts = processPrompts();

const agentsPath = Object.keys(agentsFile)[0];
const agentsContent = agentsPath ? agentsFile[agentsPath].default : '';

export const promptsFileReader = processedPrompts;

export const agentsData: PromptFile | undefined = agentsPath
  ? {
    name: 'AGENTS.md',
    fileName: 'AGENTS.md',
    type: 'pro',
    content: agentsContent
  }
  : undefined;

export const explorerData: ExplorerData = {
  prompts: processedPrompts,
  agents: agentsData
};
